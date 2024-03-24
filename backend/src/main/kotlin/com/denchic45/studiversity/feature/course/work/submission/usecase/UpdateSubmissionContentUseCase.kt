package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.stuiversity.api.submission.model.SubmissionContent
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateSubmissionContentUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

  suspend operator fun invoke(submissionId: UUID, content: SubmissionContent?) = suspendTransactionWorker {
        submissionRepository.updateSubmissionContent(submissionId, content) ?: throw NotFoundException()
    }
}