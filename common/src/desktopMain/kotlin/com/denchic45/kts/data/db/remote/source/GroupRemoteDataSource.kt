package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.GroupMap
import com.denchic45.kts.util.MutableFireMap
import kotlinx.coroutines.flow.Flow

actual class GroupRemoteDataSource {

    actual fun observeById(groupId: String): Flow<GroupMap?> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<GroupMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findById(groupId: String): GroupMap {
        TODO("Not yet implemented")
    }

    actual suspend fun findCoursesByGroupId(groupId: String): List<String> {
        TODO("Not yet implemented")
    }

    actual suspend fun removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(
        groupId: String,
        studentIds: Set<String>,
        groupCourseIds: List<String>,
    ) {
    }

    actual suspend fun updateGroupsOfCourse(groupIds: List<String>) {
    }

    actual suspend fun findByIdIn(groupIds: List<String>): List<GroupMap>? {
        TODO("Not yet implemented")
    }

    actual suspend fun removeHeadman(groupId: String) {
    }

    actual suspend fun findBySpecialtyId(specialtyId: String): List<GroupMap> {
        TODO("Not yet implemented")
    }

    actual fun findByTeacherIdAndTimestamp(
        teacherId: String,
        timestampGroups: Long,
    ): Flow<List<GroupMap>> {
        TODO("Not yet implemented")
    }

    actual fun findByCuratorId(userId: String): Flow<GroupMap> {
        TODO("Not yet implemented")
    }

    actual fun observeByCuratorId(id: String): Flow<GroupMap?> {
        TODO("Not yet implemented")
    }

    actual suspend fun findByCourse(course: Int): List<GroupMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun setHeadman(studentId: String, groupId: String) {
        TODO("Not yet implemented")
    }

    actual suspend fun add(groupMap: MutableFireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun update(groupMap: MutableFireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun updateGroupCurator(groupId: String, teacherMap: MutableFireMap) {
        TODO("Not yet implemented")
    }
}