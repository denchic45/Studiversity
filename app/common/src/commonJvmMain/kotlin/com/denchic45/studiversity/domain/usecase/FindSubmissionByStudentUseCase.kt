package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.submission.model.SubmissionResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindSubmissionByStudentUseCase(
    private val submissionRepository: SubmissionRepository,
) {
    suspend operator fun invoke(workId: UUID, studentId: UUID): Resource<SubmissionResponse> {
        return submissionRepository.findByWorkAndStudent(workId, studentId)
    }

}