package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class GradeSubmissionUseCase(
    private val submissionRepository: SubmissionRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        grade: Int
    ): Resource<SubmissionResponse> {
        return submissionRepository.gradeSubmission(courseId, workId, submissionId, grade)
    }
}