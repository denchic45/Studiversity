package com.denchic45.kts.data.remote.db

import com.denchic45.kts.data.remote.model.GroupMap
import kotlinx.coroutines.flow.Flow

actual class GroupRemoteDataSource {
    actual fun observeById(id: String): Flow<GroupMap> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<GroupMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun remove(id: String) {
    }

    actual suspend fun findById(groupId: String): GroupMap {
        TODO("Not yet implemented")
    }

    actual suspend fun findCoursesByGroupId(groupId: String): List<String> {
        TODO("Not yet implemented")
    }


}