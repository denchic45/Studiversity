package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.feature.course.subject.SubjectRepository
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(id: UUID, request: UpdateSubjectRequest): SubjectResponse {
        return subjectRepository.update(id, request) ?: throw NotFoundException()
    }
}