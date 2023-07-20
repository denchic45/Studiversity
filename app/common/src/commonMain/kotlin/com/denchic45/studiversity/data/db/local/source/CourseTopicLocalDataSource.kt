package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.CourseTopic
import com.denchic45.studiversity.entity.CourseTopicQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class CourseTopicLocalDataSource(db: AppDatabase) {

    private val queries: CourseTopicQueries = db.courseTopicQueries

    suspend fun upsert(CourseTopic: CourseTopic) = withContext(Dispatchers.IO) {
        queries.upsert(CourseTopic)
    }

    suspend fun upsert(sectionEntities: List<CourseTopic>) = withContext(Dispatchers.IO) {
        queries.transaction {
            sectionEntities.forEach { queries.upsert(it) }
        }
    }

    fun observe(topicId: String) = queries.getById(topicId).asFlow().mapToOne(Dispatchers.IO)

    fun getByCourseId(courseId: String): Flow<List<CourseTopic>> {
        return queries.getByCourseId(courseId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun deleteByCourseId(courseId: String) = withContext(Dispatchers.IO) {
        queries.deleteByCourseId(courseId)
    }


//    suspend fun deleteMissing(availableSections: List<String>) = withContext(Dispatchers.IO) {
//        queries.deleteMissing(ListMapper.fromList(availableSections))
//    }
}