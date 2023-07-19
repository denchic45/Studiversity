package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import io.ktor.server.plugins.*
import java.util.*

class FindSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

  suspend operator fun invoke(submissionId: UUID, receivingUserId: UUID) = suspendTransactionWorker {
        submissionRepository.find(submissionId)?.let { response ->
            if (response.state == SubmissionState.NEW && response.author.id == receivingUserId) {
                submissionRepository.updateSubmissionState(submissionId, SubmissionState.CREATED)
                submissionRepository.find(submissionId)!!
            } else response
        } ?: throw NotFoundException()
    }
}