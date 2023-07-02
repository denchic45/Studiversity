package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.SubmissionEntity
import com.denchic45.studiversity.SubmissionEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SubmissionLocalDataSource(db: AppDatabase) {

    private val queries: SubmissionEntityQueries = db.submissionEntityQueries

    suspend fun upsert(submissionEntity: SubmissionEntity) = withContext(Dispatchers.IO) {
        queries.upsert(submissionEntity)
    }

    suspend fun upsert(submissionEntities: List<SubmissionEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            submissionEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun deleteByContentId(id: String) = withContext(Dispatchers.IO) {
        queries.deleteByContentId(id)
    }

    fun getByWorkIdAndUserId(
        contentId: String,
        studentId: String,
    ): Flow<SubmissionEntity?> {
        return queries.getByWorkIdAndUserId(
            contentId,
            studentId
        ).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

//    fun getByWorkId(taskId: String): Flow<List<SubmissionWithStudentEntities>> {
//        return queries.getSubmissionsAndStudentsByTaskId(taskId)
//            .asFlow().mapToList(Dispatchers.IO)
//    }
}