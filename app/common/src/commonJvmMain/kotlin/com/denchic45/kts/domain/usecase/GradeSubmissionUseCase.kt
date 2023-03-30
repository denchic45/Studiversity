package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class GradeSubmissionUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID, grade: Int) {
        submissionRepository.gradeSubmission(courseId, workId, submissionId, grade)
    }
}