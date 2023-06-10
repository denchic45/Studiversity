package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class IsSubmissionAuthorUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {
    operator fun invoke(submissionId: UUID, authorId: UUID) = transactionWorker {
        submissionRepository.isAuthorBySubmissionId(submissionId, authorId)
    }
}