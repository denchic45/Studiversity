package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository
import com.stuiversity.api.course.subject.model.SubjectResponse
import io.ktor.server.plugins.*
import java.util.*

class FindSubjectByIdUseCase(private val subjectRepository: SubjectRepository) {

    operator fun invoke(id: UUID): SubjectResponse {
        return subjectRepository.findById(id) ?: throw NotFoundException()
    }
}