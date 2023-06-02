package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.Resource
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