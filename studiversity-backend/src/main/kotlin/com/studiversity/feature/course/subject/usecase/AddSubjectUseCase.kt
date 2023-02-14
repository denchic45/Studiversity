package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository
import com.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.stuiversity.api.course.subject.model.SubjectResponse

class AddSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(createSubjectRequest: CreateSubjectRequest): SubjectResponse {
        return subjectRepository.add(createSubjectRequest)
    }
}