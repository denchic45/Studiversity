package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateSubjectUseCase(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(
        subjectId: UUID,
        request: UpdateSubjectRequest
    ): Resource<SubjectResponse> {
        return subjectRepository.update(subjectId, request)
    }
}