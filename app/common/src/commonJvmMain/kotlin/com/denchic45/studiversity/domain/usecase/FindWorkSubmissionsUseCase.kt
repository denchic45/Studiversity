package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.submission.model.SubmissionByAuthor
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindWorkSubmissionsUseCase(
    private val submissionRepository: SubmissionRepository,
) {

    suspend operator fun invoke(workId: UUID): Resource<List<SubmissionByAuthor>> {
        return submissionRepository.findSubmissionsByWork(workId)
    }
}