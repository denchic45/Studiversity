package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class RequireSubmissionAuthorUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {
  operator fun invoke(submissionId: UUID, authorId: UUID) = transactionWorker {
        if (!submissionRepository.isAuthorBySubmissionId(submissionId, authorId)) {
            throw ForbiddenException()
        }
    }
}