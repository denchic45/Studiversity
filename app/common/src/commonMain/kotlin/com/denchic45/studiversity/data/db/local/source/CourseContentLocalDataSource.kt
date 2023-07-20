package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.ContentCommentEntity
import com.denchic45.studiversity.entity.CourseContentEntity
import com.denchic45.studiversity.entity.CourseContentEntityQueries
import com.denchic45.studiversity.entity.Submission
import com.denchic45.studiversity.entity.SubmissionCommentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.util.Date

@Inject
class CourseContentLocalDataSource(private val db: AppDatabase) {

    private val queries: CourseContentEntityQueries = db.courseContentEntityQueries

    suspend fun deleteByIds(courseContentEntity: CourseContentEntity) =
        withContext(Dispatchers.IO) {
            queries.delete(courseContentEntity.content_id)
        }

    private fun deleteByIds(courseContentEntities: List<String>) = queries.transaction {
        courseContentEntities.forEach(queries::delete)
    }

    fun upsert(courseContentEntity: CourseContentEntity) =
//        withContext(Dispatchers.IO) {
        queries.upsert(courseContentEntity)
//    }

    fun upsert(courseContentEntities: List<CourseContentEntity>) =

        queries.transaction {
            courseContentEntities.forEach(queries::upsert)
        }

    fun getByCourseId(courseId: String): Flow<List<CourseContentEntity>> {
        return queries.getByCourseId(courseId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun get(id: String): CourseContentEntity? = withContext(Dispatchers.IO) {
        queries.getById(id).executeAsOneOrNull()
    }

    fun observe(id: String): Flow<CourseContentEntity?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    fun getAttachmentsById(id: String): Flow<List<String>> {
        return queries.getAttachmentsById(id).asFlow()
            .mapToOneOrDefault(emptyList(), Dispatchers.IO)
    }

    suspend fun getCourseIdByTaskId(taskId: String) = withContext(Dispatchers.IO) {
        queries.getCourseIdByTaskId(taskId).executeAsOne()
    }


    fun getByGroupIdAndGreaterCompletionDate(
        groupId: String,
        startDate: Date = Date(),
    ): Flow<List<CourseContentEntity>> {
        return queries.getByGroupIdAndGreaterCompletionDate(startDate.time, groupId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun getByGroupIdAndNotSubmittedUser(
        groupId: String,
        studentId: String,
        endDate: Date = Date(),
    ): Flow<List<CourseContentEntity>> {
        return queries.getByGroupIdAndNotSubmittedUser(endDate.time, groupId, studentId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun getByGroupIdAndSubmittedUser(
        groupId: String,
        studentId: String,
    ): Flow<List<CourseContentEntity>> {
        return queries.getByGroupIdAndSubmittedUser(groupId, studentId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun saveContents(
        removedCourseContentIds: List<String>,
        remainingCourseContent: List<CourseContentEntity>,
        contentIds: List<String>,
        submissionEntities: List<Submission>,
        contentCommentEntities: List<ContentCommentEntity>,
        submissionCommentEntities: List<SubmissionCommentEntity>,
    ) = withContext(Dispatchers.IO) {
        db.transaction {
            contentIds.forEach {
                db.submissionQueries.deleteByContentId(it)
                db.contentCommentEntityQueries.deleteByContentId(it)
            }
            deleteByIds(removedCourseContentIds)
            upsert(remainingCourseContent)
            submissionEntities.forEach {
                db.submissionQueries.upsert(it)
            }
            contentCommentEntities.forEach {
                db.contentCommentEntityQueries.upsert(it)
            }
            submissionCommentEntities.forEach {
                db.submissionCommentEntityQueries.upsert(it)
            }
        }
    }
}