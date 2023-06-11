package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.feature.course.subject.SubjectRepository
import io.ktor.server.plugins.*
import java.util.*

class RemoveSubjectUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(id: UUID) {
        subjectRepository.remove(id) ?: throw NotFoundException()
    }
}