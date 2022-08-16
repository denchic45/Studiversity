package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.data.db.remote.source.UserRemoteDataSource
import com.denchic45.kts.data.mapper.toMap
import com.denchic45.kts.data.mapper.toUserDomain
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.remote.UserRemoteStorage
import com.denchic45.kts.domain.model.User
import javax.inject.Inject

class StudentRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val networkService: NetworkService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userRemoteStorage: UserRemoteStorage,
) : Repository() {

    suspend fun add(student: User) {
        requireAllowWriteData()
        userRemoteDataSource.addStudent(UserMap(student.toMap()))
    }

    suspend fun update(student: User) {
        requireAllowWriteData()
        userRemoteDataSource.updateStudent(
            oldStudentMap = UserMap(userLocalDataSource.get(student.id)!!.toUserDomain().toMap()),
            studentMap = UserMap(student.toMap())
        )
    }

    suspend fun remove(studentId: String) {
        requireAllowWriteData()
        userRemoteDataSource.removeStudent(studentId, getGroupIdByStudentId(studentId)!!)
        userRemoteStorage.deleteAvatar(studentId)
    }

    private suspend fun getGroupIdByStudentId(studentId: String): String? {
        return userLocalDataSource.getGroupId(studentId)
    }
}