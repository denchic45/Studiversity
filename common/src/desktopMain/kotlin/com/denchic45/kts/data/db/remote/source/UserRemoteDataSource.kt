package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.util.FireMap
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
actual class UserRemoteDataSource @Inject constructor() {
    actual fun observeById(id: String): Flow<UserMap?> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<UserMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findAndByEmail(email: String): UserMap {
        TODO("Not yet implemented")
    }

    actual fun findTeachersByContainsName(text: String): Flow<List<UserMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun addTeacher(map: FireMap) {

    }

    actual suspend fun updateTeacher(teacherMap: FireMap) {

    }

    actual suspend fun removeTeacher(teacher: FireMap) {

    }

    actual suspend fun findById(userId: String): UserMap {
        TODO("Not yet implemented")
    }

    actual suspend fun addStudent(studentMap: UserMap) {
    }

    actual suspend fun updateStudent(
        oldStudentMap: UserMap,
        studentMap: UserMap,
    ) {
    }

    actual suspend fun removeStudent(studentId: String, groupId: String) {
    }

}