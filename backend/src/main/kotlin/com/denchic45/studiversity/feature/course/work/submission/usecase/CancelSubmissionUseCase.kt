package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import io.ktor.server.plugins.*
import java.util.*

class CancelSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    suspend operator fun invoke(submissionId: UUID, studentId: UUID) = suspendTransactionWorker {
        val currentSubmission = submissionRepository.find(submissionId) ?: throw NotFoundException()
        if (currentSubmission.author.id != studentId)
            throw BadRequestException("INVALID_AUTHOR")
        if (currentSubmission.state in SubmissionState.notSubmitted())
            throw BadRequestException("NOT_SUBMITTED")
        if (currentSubmission.grade != null)
            throw BadRequestException("SUBMISSION_GRADED")
        submissionRepository.cancelSubmission(submissionId)
    }
}