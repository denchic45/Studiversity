package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.feature.course.subject.SubjectRepository
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import io.ktor.server.plugins.*
import java.util.*

class FindSubjectByIdUseCase(private val subjectRepository: SubjectRepository) {

  suspend operator fun invoke(id: UUID): SubjectResponse {
        return subjectRepository.findById(id) ?: throw NotFoundException()
    }
}