package com.denchic45.kts.data.db.remote.source

import android.util.Log
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.GroupMap
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.util.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

actual class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val usersRef: CollectionReference = firestore.collection("Users")
    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")

    actual suspend fun findById(id: String): UserMap {
        return usersRef.document(id).get().await().toMap(::UserMap)
    }

    actual fun observeById(id: String): Flow<UserMap?> = usersRef.document(id)
        .getDocumentSnapshotFlow()
        .map { it.toMap(::UserMap) }

    actual fun findByContainsName(text: String): Flow<List<UserMap>> {
        return usersRef
            .whereArrayContains("searchKeys", SearchKeysGenerator.formatInput(text))
            .getQuerySnapshotFlow()
            .map { it.toMaps(::UserMap) }
    }

    actual suspend fun findByEmail(email: String): UserMap {
        return usersRef.whereEqualTo("email", email)
            .get()
            .await().run {
                Log.d("lol", "A whereEqualTo awaited: ")
                if (isEmpty) {
                    throw  FirebaseAuthException(
                        "ERROR_USER_NOT_FOUND",
                        "Nothing user!"
                    )
                }
                documents[0].toMap(::UserMap)
            }
    }

    actual suspend fun addTeacher(map: FireMap) {
        firestore.batch().apply {
            set(usersRef.document(map["id"] as String), map)
            commit().await()
        }
    }

    actual suspend fun updateTeacher(teacherMap: FireMap) {
        firestore.batch().apply {
            val teacherId = teacherMap["id"] as String
            val tasks = Tasks.whenAllComplete(
                findCoursesWithTeacherQuery(teacherId),
                findGroupWithCuratorQuery(teacherId)
            ).await()
            val coursesWithThisTeacherSnapshot = tasks[0].result as QuerySnapshot?
            val snapshot = tasks[1].result as QuerySnapshot?
            if (!coursesWithThisTeacherSnapshot!!.isEmpty) {
                val coursesWithThisTeacher = coursesWithThisTeacherSnapshot.toMaps(::CourseMap)
                for (courseDoc in coursesWithThisTeacher) {
                    val updateCourseMap: MutableMap<String, Any> = HashMap()
                    updateCourseMap["teacher"] = teacherMap
                    updateCourseMap["timestamp"] = FieldValue.serverTimestamp()
                    update(coursesRef.document(courseDoc.id), updateCourseMap)
                }
            }
            if (!snapshot!!.isEmpty) {
                val groupWithThisCurator = snapshot.documents[0].toMap(::GroupMap)
                val updateGroupMap: MutableMap<String, Any> = HashMap()
                updateGroupMap["curator"] = teacherMap
                updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
                update(groupsRef.document(groupWithThisCurator.id), updateGroupMap)
            }
            this[usersRef.document(teacherMap["id"] as String)] = teacherMap
            commit().await()
        }
    }

    private fun findCoursesWithTeacherQuery(teacherId: String): Task<QuerySnapshot> {
        return coursesRef.whereEqualTo("teacher.id", teacherId).get()
    }

    private fun findGroupWithCuratorQuery(teacherId: String): Task<QuerySnapshot> {
        return groupsRef.whereEqualTo("curator.id", teacherId).get()
    }

    actual suspend fun removeTeacher(teacher: FireMap) {
        firestore.batch().apply {
            delete(usersRef.document(teacher["id"] as String))
            commit().await()
        }
    }

    actual fun findTeachersByContainsName(text: String): Flow<List<UserMap>> {
        return usersRef.whereArrayContains(
            "searchKeys",
            SearchKeysGenerator.formatInput(text)
        )
            .whereEqualTo("teacher", true)
            .getDataFlow { it.toMaps(::UserMap) }
    }

    actual suspend fun addStudent(studentMap: UserMap) {
        firestore.batch().apply {
            this[usersRef.document(studentMap.id)] = studentMap.map
            updateStudentFromGroup(studentMap.map, studentMap.groupId!!, this)
            commit().await()
        }
    }

    private fun updateStudentFromGroup(studentMap: FireMap, groupId: String, batch: WriteBatch) {
        val updateGroupMap: MutableMap<String, Any> = HashMap()
        updateGroupMap["students." + studentMap["id"]] = studentMap
        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
        batch.update(groupsRef.document(groupId), updateGroupMap)
    }

    actual suspend fun updateStudent(oldStudentMap: UserMap, studentMap: UserMap) {
        firestore.batch().apply {
            if (changePersonalData(studentMap, oldStudentMap)) {
                this[usersRef.document(studentMap.id)] = studentMap.map
            }
            if (changeGroup(studentMap, oldStudentMap)) {
                deleteStudentFromGroup(studentMap.id, oldStudentMap.groupId!!, this)
            }
            updateStudentFromGroup(studentMap.map, studentMap.groupId!!, this)
            commit().await()
        }
    }

    private fun deleteStudentFromGroup(studentId: String, groupId: String, batch: WriteBatch) {
        val updateGroupMap: MutableMap<String, Any> = HashMap()
        updateGroupMap["students.$studentId"] = FieldValue.delete()
        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
        batch.update(groupsRef.document(groupId), updateGroupMap)
    }

    private fun changePersonalData(student: UserMap, cacheStudent: UserMap): Boolean {
        return student.fullName != cacheStudent.fullName || student.gender != cacheStudent.gender ||
                student.firstName != cacheStudent.firstName ||
                student.email != cacheStudent.email ||
                student.photoUrl != cacheStudent.photoUrl || student.admin != cacheStudent.admin ||
                student.role != cacheStudent.role
    }

    private fun changeGroup(student: UserMap, oldStudent: UserMap): Boolean {
        return student.groupId != oldStudent.groupId
    }

    actual suspend fun removeStudent(studentId: String, groupId: String) {
        val batch = firestore.batch()
        batch.delete(usersRef.document(studentId))
        deleteStudentFromGroup(studentId, groupId, batch)
        batch.commit().await()
    }
}