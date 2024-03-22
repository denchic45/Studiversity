package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubmissionRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class ObserveYourSubmissionUseCase(
    private val submissionRepository: SubmissionRepository
) {
    operator fun invoke(courseId: UUID, workId: UUID): Flow<Resource<SubmissionResponse>> {
        return submissionRepository.findOwnSubmissionByWork(courseId, workId)
    }
}