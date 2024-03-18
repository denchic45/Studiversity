package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CancelGradeSubmissionUseCase(
    private val submissionRepository: SubmissionRepository
) {
    suspend operator fun invoke(submissionId: UUID): EmptyResource {
        return submissionRepository.removeSubmissionGrade(submissionId)
    }
}