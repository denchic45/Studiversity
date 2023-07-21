package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.submission.SubmissionErrors
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import io.ktor.server.plugins.*
import java.util.*

class SubmitSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    suspend operator fun invoke(submissionId: UUID, studentId: UUID) = suspendTransactionWorker {
        val currentSubmission = submissionRepository.find(submissionId) ?: throw NotFoundException()
        if (currentSubmission.author.id != studentId)
            throw BadRequestException(SubmissionErrors.INVALID_AUTHOR)
        if (currentSubmission.state == SubmissionState.SUBMITTED)
            throw BadRequestException(SubmissionErrors.ALREADY_SUBMITTED)
        submissionRepository.submitSubmission(submissionId)
    }
}