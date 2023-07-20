package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.SubmissionCommentEntity
import com.denchic45.studiversity.entity.SubmissionCommentEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SubmissionCommentLocalDataSource(db: AppDatabase) {

    private val queries: SubmissionCommentEntityQueries = db.submissionCommentEntityQueries

    suspend fun upsert(submissionCommentEntity: SubmissionCommentEntity) =
        withContext(Dispatchers.IO) {
            queries.upsert(submissionCommentEntity)
        }

    suspend fun upsert(submissionCommentEntities: List<SubmissionCommentEntity>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                submissionCommentEntities.forEach { queries.upsert(it) }
            }
        }
}