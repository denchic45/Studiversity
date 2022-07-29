package com.denchic45.kts.data.remote.db

import com.denchic45.kts.data.remote.model.GroupMap
import kotlinx.coroutines.flow.Flow

expect class GroupRemoteDataSource {

    fun observeById(id: String): Flow<GroupMap>

    fun findByContainsName(text: String): Flow<List<GroupMap>>

    suspend fun remove(id: String)

   suspend fun findById(groupId: String): GroupMap

    suspend fun findCoursesByGroupId(groupId: String): List<String>
}