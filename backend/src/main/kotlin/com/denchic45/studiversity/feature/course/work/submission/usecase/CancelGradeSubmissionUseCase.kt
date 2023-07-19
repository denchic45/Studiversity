package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class CancelGradeSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    suspend operator fun invoke(submissionId: UUID) = suspendTransactionWorker {
//        val currentSubmission = submissionRepository.find(submissionId) ?: throw NotFoundException()
        submissionRepository.removeGradeSubmission(submissionId)
    }
}