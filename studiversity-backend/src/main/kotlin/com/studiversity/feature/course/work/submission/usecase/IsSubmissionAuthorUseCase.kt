package com.studiversity.feature.course.work.submission.usecase

import com.studiversity.feature.course.work.submission.SubmissionRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class IsSubmissionAuthorUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {
    operator fun invoke(submissionId: UUID, authorId: UUID) = transactionWorker {
        submissionRepository.isAuthorBySubmissionId(submissionId, authorId)
    }
}