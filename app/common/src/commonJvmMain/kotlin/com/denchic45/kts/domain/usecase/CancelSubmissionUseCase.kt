package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import java.util.*

class CancelSubmissionUseCase(private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID) {
        submissionRepository.cancelSubmission(courseId, workId, submissionId)
    }
}