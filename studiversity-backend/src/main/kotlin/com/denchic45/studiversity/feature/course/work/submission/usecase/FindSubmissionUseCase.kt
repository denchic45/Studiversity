package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindSubmissionUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    operator fun invoke(submissionId: UUID, receivingUserId: UUID) = transactionWorker {
        submissionRepository.find(submissionId)?.let { response ->
            if (response.state == SubmissionState.NEW && response.author.id == receivingUserId) {
                submissionRepository.updateSubmissionState(submissionId, SubmissionState.CREATED)
                submissionRepository.find(submissionId)!!
            } else response
        } ?: throw NotFoundException()
    }
}