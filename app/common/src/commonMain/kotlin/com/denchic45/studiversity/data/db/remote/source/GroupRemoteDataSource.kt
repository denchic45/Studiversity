package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.GroupMap
import com.denchic45.studiversity.util.MutableFireMap
import kotlinx.coroutines.flow.Flow

expect class GroupRemoteDataSource {

    fun observeById(id: String): Flow<GroupMap?>

    suspend fun add(groupMap: MutableFireMap)

    suspend fun update(groupMap: MutableFireMap)

    fun findByContainsName(text: String): Flow<List<GroupMap>>

    suspend fun findById(id: String): GroupMap

    suspend fun findCoursesByGroupId(groupId: String): List<String>

    suspend fun removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(
        groupId: String,
        studentIds: Set<String>,
        groupCourseIds: List<String>,
    )

    suspend fun updateGroupsOfCourse(groupIds: List<String>)

    suspend fun findByIdIn(groupIds: List<String>): List<GroupMap>?

    suspend fun setHeadman(studentId: String, groupId: String)

    suspend fun removeHeadman(groupId: String)

    suspend fun updateGroupCurator(groupId: String, teacherMap: MutableFireMap)

    suspend fun findBySpecialtyId(specialtyId: String): List<GroupMap>

    fun findByTeacherIdAndTimestamp(
        teacherId: String,
        timestampGroups: Long,
    ): Flow<List<GroupMap>>

    suspend fun findByCuratorId(id: String): GroupMap

    fun observeByCuratorId(id: String): Flow<GroupMap?>

    suspend fun findByCourse(course: Int): List<GroupMap>
}