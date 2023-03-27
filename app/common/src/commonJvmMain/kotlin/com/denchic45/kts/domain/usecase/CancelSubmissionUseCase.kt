package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import java.util.*

class CancelSubmissionUseCase(private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID): Resource<SubmissionResponse> {
       return submissionRepository.cancelSubmission(courseId, workId, submissionId)
    }
}