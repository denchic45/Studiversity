package com.studiversity.feature.course.work.submission.usecase

import com.studiversity.feature.course.work.submission.SubmissionRepository
import com.studiversity.ktor.ForbiddenException
import com.studiversity.transaction.TransactionWorker
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