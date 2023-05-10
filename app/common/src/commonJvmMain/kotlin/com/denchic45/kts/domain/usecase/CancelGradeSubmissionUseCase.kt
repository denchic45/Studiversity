package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import com.denchic45.kts.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CancelGradeSubmissionUseCase @javax.inject.Inject constructor(
    private val submissionRepository: SubmissionRepository
) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID): EmptyResource {
        return submissionRepository.removeSubmissionGrade(courseId, workId, submissionId)
    }
}