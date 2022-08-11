package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.GroupCourseEntity
import com.denchic45.kts.GroupCourseEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupCourseLocalDataSource @Inject constructor(db: AppDatabase) {
    private val queries: GroupCourseEntityQueries = db.groupCourseEntityQueries

    suspend fun upsert(groupCourseEntity: GroupCourseEntity) = withContext(Dispatchers.IO) {
        queries.upsert(groupCourseEntity)
    }

    fun upsert(groupCourseEntities: List<GroupCourseEntity>) {
//        withContext(Dispatchers.IO) {
        queries.transaction {
            groupCourseEntities.forEach { queries.upsert(it) }
        }
//        }
    }

    suspend fun deleteByGroup(groupId: String) = withContext(Dispatchers.IO) {
        queries.deleteByGroupId(groupId)
    }

    fun deleteByCourseId(courseId: String) {
//         withContext(Dispatchers.IO) {
        queries.deleteByCourseId(courseId)
//         }
    }
}