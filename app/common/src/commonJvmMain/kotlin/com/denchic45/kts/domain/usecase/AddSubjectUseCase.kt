package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.EmptyResource
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
) {

    suspend operator fun invoke(request: CreateSubjectRequest): Resource<SubjectResponse> {
        return subjectRepository.add(request)
    }
}