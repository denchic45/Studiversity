package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.StudyGroupCourse
import com.denchic45.studiversity.entity.StudyGroupCourseQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupCourseLocalDataSource(db: AppDatabase) {
    private val queries: StudyGroupCourseQueries = db.studyGroupCourseQueries

    suspend fun upsert(groupCourseEntity: StudyGroupCourse) = withContext(Dispatchers.IO) {
        queries.upsert(groupCourseEntity)
    }

    fun upsert(groupCourseEntities: List<StudyGroupCourse>) {
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