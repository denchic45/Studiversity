package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository
import com.stuiversity.api.course.subject.model.SubjectResponse
import com.stuiversity.api.course.subject.model.UpdateSubjectRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(id: UUID, request: UpdateSubjectRequest): SubjectResponse {
        return subjectRepository.update(id, request) ?: throw NotFoundException()
    }
}