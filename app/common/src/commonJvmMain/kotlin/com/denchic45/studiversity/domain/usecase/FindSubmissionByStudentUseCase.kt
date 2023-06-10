package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSubmissionByStudentUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        studentId: UUID,
    ): Resource<SubmissionResponse> {
        return submissionRepository.findByWorkAndStudent(courseId, workId, studentId)
    }

}