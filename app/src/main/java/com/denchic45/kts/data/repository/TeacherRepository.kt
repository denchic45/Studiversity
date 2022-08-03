package com.denchic45.kts.data.repository

import android.util.Log
import com.denchic45.kts.data.mapper.mapsToUsers
import com.denchic45.kts.data.mapper.toMap
import com.denchic45.kts.data.remote.model.CourseMap
import com.denchic45.kts.data.remote.model.GroupMap
import com.denchic45.kts.data.remote.model.UserMap
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.SearchKeysGenerator
import com.denchic45.kts.util.getDataFlow
import com.denchic45.kts.util.toMap
import com.denchic45.kts.util.toMaps
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeacherRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val networkService: NetworkService,
) : Repository(), FindByContainsNameRepository<User> {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = firestore.collection("Users")
    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val coursesRef: CollectionReference = firestore.collection("Courses")

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val avatarsRef: StorageReference = storage.reference.child("avatars")
    private var batch: WriteBatch? = null

    override fun findByContainsName(text: String): Flow<List<User>> {
        requireNetworkAvailable()
        return usersRef.whereArrayContains(
            "searchKeys",
            SearchKeysGenerator.formatInput(text)
        )
            .whereEqualTo("teacher", true)
            .getDataFlow { it.toMaps(::UserMap).mapsToUsers() }
    }

    suspend fun add(teacher: User): User {
        batch = firestore.batch()
        val teacherMap = teacher.toMap()
        batch!![usersRef.document(teacherMap["id"] as String)] = teacherMap
        batch!!.commit().await()
        return teacher
    }

    suspend fun update(teacher: User): User {
        batch = firestore.batch()
        val teacherMap = teacher.toMap()
        val tasks = Tasks.whenAllComplete(
            findCoursesWithTeacherQuery(teacher.id),
            findGroupWithCuratorQuery(teacher.id)
        ).await()
        val coursesWithThisTeacherSnapshot = tasks[0].result as QuerySnapshot?
        val snapshot = tasks[1].result as QuerySnapshot?
        if (!coursesWithThisTeacherSnapshot!!.isEmpty) {
            val coursesWithThisTeacher = coursesWithThisTeacherSnapshot.toMaps(::CourseMap)
            for (courseDoc in coursesWithThisTeacher) {
                val updateCourseMap: MutableMap<String, Any> = HashMap()
                updateCourseMap["teacher"] = teacherMap
                updateCourseMap["timestamp"] = FieldValue.serverTimestamp()
                batch!!.update(coursesRef.document(courseDoc.id), updateCourseMap)
            }
        }
        if (!snapshot!!.isEmpty) {
            val groupWithThisCurator = snapshot.documents[0].toMap(::GroupMap)
            val updateGroupMap: MutableMap<String, Any> = HashMap()
            updateGroupMap["curator"] = teacherMap
            updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
            batch!!.update(groupsRef.document(groupWithThisCurator.id), updateGroupMap)
        }
        batch!![usersRef.document(teacherMap["id"] as String)] = teacherMap
        batch!!.commit().await()
        return teacher
    }

    private fun findCoursesWithTeacherQuery(teacherId: String): Task<QuerySnapshot> {
        return coursesRef.whereEqualTo("teacher.id", teacherId).get()
    }

    private fun findGroupWithCuratorQuery(teacherId: String): Task<QuerySnapshot> {
        return groupsRef.whereEqualTo("curator.id", teacherId).get()
    }


    suspend fun remove(teacher: User) {
        requireAllowWriteData()
        batch = firestore.batch()
        batch!!.delete(usersRef.document(teacher.id))
        batch!!.commit()
            .await()
        deleteAvatar(teacher.id)
    }

    private fun deleteAvatar(userId: String) {
        val reference = avatarsRef.child(userId)
        reference.delete().addOnSuccessListener { aVoid: Void? -> Log.d("lol", "onSuccess: ") }
            .addOnFailureListener { e: Exception? -> Log.d("lol", "onFailure: ", e) }
    }

}