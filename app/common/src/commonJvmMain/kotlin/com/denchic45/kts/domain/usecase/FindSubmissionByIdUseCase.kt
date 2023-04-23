package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSubmissionByIdUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
    ): Resource<SubmissionResponse> {
        return submissionRepository.findById(courseId, workId, submissionId)
    }

}