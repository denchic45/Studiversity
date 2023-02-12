package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.SubmissionCommentEntity
import com.denchic45.kts.SubmissionCommentEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubmissionCommentLocalDataSource @Inject constructor(db: AppDatabase) {

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