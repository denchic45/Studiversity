package com.denchic45.kts.data.repository

import com.denchic45.kts.data.local.db.UserLocalDataSource
import com.denchic45.kts.data.mapper.toMap
import com.denchic45.kts.data.mapper.toUserDomain
import com.denchic45.kts.data.remote.model.UserMap
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.FireMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val networkService: NetworkService,
    private val userLocalDataSource: UserLocalDataSource,
    private val firestore: FirebaseFirestore,
) : Repository() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val avatarRef: StorageReference = storage.reference.child("avatars")
    private val userRef: CollectionReference = firestore.collection("Users")
    private val groupRef: CollectionReference = firestore.collection("Groups")

    suspend fun add(student: User) {
        requireAllowWriteData()
        val batch = firestore.batch()
        val studentMap = student.toMap()
        batch[userRef.document(student.id)] = studentMap
        updateStudentFromGroup(studentMap, student.groupId!!, batch)
        batch.commit().await()
    }

    suspend fun update(student: User) {
        requireAllowWriteData()
        val batch = firestore.batch()
        val studentMap = UserMap(student.toMap())
        val cacheStudent = userLocalDataSource.get(student.id)!!.toUserDomain()
        if (changePersonalData(student, cacheStudent)) {
            batch[userRef.document(student.id)] = studentMap
        }
        if (changeGroup(student, cacheStudent)) {
            deleteStudentFromGroup(studentMap.id, cacheStudent.groupId!!, batch)
        }
        updateStudentFromGroup(studentMap.map, student.groupId!!, batch)
        batch.commit().await()
    }

    private fun changePersonalData(student: User, cacheStudent: User): Boolean {
        return student.fullName != cacheStudent.fullName || student.gender != cacheStudent.gender ||
                student.firstName != cacheStudent.firstName ||
                student.email != cacheStudent.email ||
                student.photoUrl != cacheStudent.photoUrl || student.admin != cacheStudent.admin ||
                student.role != cacheStudent.role
    }

    private fun changeGroup(student: User, cacheStudent: User): Boolean {
        return student.groupId != cacheStudent.groupId
    }

    suspend fun remove(studentId: String) {
        requireAllowWriteData()
        val batch = firestore.batch()
        batch.delete(userRef.document(studentId))
        deleteStudentFromGroup(studentId, getGroupIdByStudentId(studentId)!!, batch)
        batch.commit().await()
        deleteAvatar(studentId)
    }

    private suspend fun getGroupIdByStudentId(studentId: String): String? {
        return userLocalDataSource.getGroupId(studentId)
    }

    private suspend fun deleteAvatar(userId: String) {
        val reference = avatarRef.child(userId)
        reference.delete().await()
    }

    private fun updateStudentFromGroup(studentMap: FireMap, groupId: String, batch: WriteBatch) {
        val updateGroupMap: MutableMap<String, Any> = HashMap()
        updateGroupMap["students." + studentMap["id"]] = studentMap
        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
        batch.update(groupRef.document(groupId), updateGroupMap)
    }

    private fun deleteStudentFromGroup(studentId: String, groupId: String, batch: WriteBatch) {
        val updateGroupMap: MutableMap<String, Any> = HashMap()
        updateGroupMap["students.$studentId"] = FieldValue.delete()
        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
        batch.update(groupRef.document(groupId), updateGroupMap)
    }
}