package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.denchic45.stuiversity.api.submission.SubmissionErrors
import io.ktor.server.plugins.*

class SetGradeSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

    suspend operator fun invoke(grade: SubmissionGradeRequest) = suspendTransactionWorker {
        if (0 > grade.value || grade.value > 5)
            throw BadRequestException(SubmissionErrors.INVALID_GRADE)
        submissionRepository.setGradeSubmission(grade)
    }
}