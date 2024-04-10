package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.feature.course.subject.SubjectRepository

class AddSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(createSubjectRequest: CreateSubjectRequest): SubjectResponse {
        return subjectRepository.add(createSubjectRequest)
    }
}