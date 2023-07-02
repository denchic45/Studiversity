package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseWorkSubmissionsUseCase(
    private val submissionRepository: SubmissionRepository,
) {

    suspend operator fun invoke(courseId: UUID, workId: UUID): Resource<List<SubmissionResponse>> {
        return submissionRepository.findSubmissionsByWork(courseId, workId)
    }
}