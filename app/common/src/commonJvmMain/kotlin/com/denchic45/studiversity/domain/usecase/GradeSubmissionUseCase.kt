package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class GradeSubmissionUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, submissionId: UUID, grade: Int): Resource<SubmissionResponse> {
       return submissionRepository.gradeSubmission(courseId, workId, submissionId, grade)
    }
}