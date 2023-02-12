package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.util.FireMap
import kotlinx.coroutines.flow.Flow

expect class UserRemoteDataSource {

    suspend fun findById(id: String): UserMap

    fun observeById(id: String): Flow<UserMap?>

    fun findByContainsName(text: String): Flow<List<UserMap>>

    suspend fun findByEmail(email: String): UserMap

    suspend fun addTeacher(map: FireMap)

    suspend fun updateTeacher(teacherMap: FireMap)

    suspend fun removeTeacher(teacher: FireMap)

    fun findTeachersByContainsName(text: String): Flow<List<UserMap>>

    suspend fun addStudent(studentMap: UserMap)

    suspend fun updateStudent(oldStudentMap: UserMap, studentMap: UserMap)

    suspend fun removeStudent(studentId: String, groupId: String)
}