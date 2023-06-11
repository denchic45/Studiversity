package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionContent
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateSubmissionContentUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    operator fun invoke(submissionId: UUID, content: SubmissionContent?) = transactionWorker {
        submissionRepository.updateSubmissionContent(submissionId, content) ?: throw NotFoundException()
    }
}