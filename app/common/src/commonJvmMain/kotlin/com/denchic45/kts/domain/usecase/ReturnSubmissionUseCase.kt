package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import java.util.UUID
import javax.inject.Inject

class ReturnSubmissionUseCase @Inject constructor(private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId:UUID, workId: UUID, submissionId: UUID) {
        submissionRepository.returnSubmission(courseId,workId, submissionId)
    }
}