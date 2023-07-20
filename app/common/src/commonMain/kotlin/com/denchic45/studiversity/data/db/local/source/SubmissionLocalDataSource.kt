package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Submission
import com.denchic45.studiversity.entity.SubmissionQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SubmissionLocalDataSource(db: AppDatabase) {

    private val queries: SubmissionQueries = db.submissionQueries

    suspend fun upsert(submissionEntity: Submission) =
        withContext(Dispatchers.IO) {
            queries.upsert(submissionEntity)
        }

    suspend fun upsert(submissionEntities: List<Submission>) = withContext(Dispatchers.IO) {
        queries.transaction {
            submissionEntities.forEach { queries.upsert(it) }
        }
    }

//    suspend fun deleteByContentId(id: String) = withContext(Dispatchers.IO) {
//        queries.deleteByContentId(id)
//    }

//    fun getByWorkIdAndUserId(
//        contentId: String,
//        studentId: String,
//    ): Flow<Submission?> {
//        return queries.getByWorkIdAndUserId(
//            contentId,
//            studentId
//        ).asFlow().mapToOneOrNull(Dispatchers.IO)
//    }

//    fun getByWorkId(taskId: String): Flow<List<SubmissionWithStudentEntities>> {
//        return queries.getSubmissionsAndStudentsByTaskId(taskId)
//            .asFlow().mapToList(Dispatchers.IO)
//    }
}