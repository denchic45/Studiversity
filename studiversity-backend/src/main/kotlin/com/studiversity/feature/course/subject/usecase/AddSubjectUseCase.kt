package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository
import com.stuiversity.api.course.subject.model.CreateSubjectRequest
import java.util.UUID

class AddSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(createSubjectRequest: CreateSubjectRequest):UUID {
        return subjectRepository.add(createSubjectRequest)
    }
}