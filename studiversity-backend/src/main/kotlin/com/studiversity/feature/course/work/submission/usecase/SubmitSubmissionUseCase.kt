package com.studiversity.feature.course.work.submission.usecase

import com.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class SubmitSubmissionUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    operator fun invoke(submissionId: UUID, studentId: UUID) = transactionWorker {
        val currentSubmission = submissionRepository.find(submissionId) ?: throw NotFoundException()
        if (currentSubmission.authorId != studentId)
            throw BadRequestException("INVALID_AUTHOR")
        if (currentSubmission.state == SubmissionState.SUBMITTED)
            throw BadRequestException("ALREADY_SUBMITTED")
        submissionRepository.submitSubmission(submissionId)
    }
}