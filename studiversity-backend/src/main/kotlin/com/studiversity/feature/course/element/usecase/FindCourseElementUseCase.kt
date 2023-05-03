package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.course.element.CourseElementRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseElementUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(elementId: UUID) = transactionWorker {
        courseElementRepository.findById(elementId) ?: throw NotFoundException()
    }
}