package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseElementUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {

    suspend operator fun invoke(elementId: UUID) = suspendTransactionWorker {
        courseElementRepository.findById(elementId) ?: throw NotFoundException()
    }
}