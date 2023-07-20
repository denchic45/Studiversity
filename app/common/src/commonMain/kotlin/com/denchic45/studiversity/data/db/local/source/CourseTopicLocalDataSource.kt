package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.denchic45.studiversity.data.db.local.suspendedTransaction
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

    suspend fun upsert(topic: CourseTopic) = withContext(Dispatchers.IO) {
        queries.upsert(topic)
    }

    suspend fun upsert(courseTopics: List<CourseTopic>) = withContext(Dispatchers.IO) {
        queries.transaction {
            courseTopics.forEach { queries.upsert(it) }
        }
    }

    suspend fun upsertByCourseId(
        courseTopics: List<CourseTopic>,
        courseId: String
    ) = queries.suspendedTransaction {
        deleteByCourseId(courseId)
        upsert(courseTopics)
    }


    fun observe(topicId: String) = queries.getById(topicId).asFlow().mapToOne(Dispatchers.IO)

    fun getByCourseId(courseId: String): Flow<List<CourseTopic>> {
        return queries.getByCourseId(courseId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun deleteById(topicId: String) = withContext(Dispatchers.IO) {
        queries.deleteById(topicId)
    }

    suspend fun deleteByCourseId(courseId: String) = withContext(Dispatchers.IO) {
        queries.deleteByCourseId(courseId)
    }
}