package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddSubjectUseCase(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(request: CreateSubjectRequest): Resource<SubjectResponse> {
        return subjectRepository.add(request)
    }
}