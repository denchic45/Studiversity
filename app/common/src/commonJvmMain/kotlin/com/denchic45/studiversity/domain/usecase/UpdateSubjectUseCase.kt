package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UpdateSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(
        subjectId: UUID,
        request: UpdateSubjectRequest
    ): Resource<SubjectResponse> {
        return subjectRepository.update(subjectId, request)
    }
}