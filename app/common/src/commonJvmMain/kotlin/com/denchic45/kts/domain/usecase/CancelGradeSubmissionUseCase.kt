package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CancelGradeSubmissionUseCase @javax.inject.Inject constructor(
    private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID) {
        submissionRepository.removeSubmissionGrade(courseId, workId, submissionId)
    }
}