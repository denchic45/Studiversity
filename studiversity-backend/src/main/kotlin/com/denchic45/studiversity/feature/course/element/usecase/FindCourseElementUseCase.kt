package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
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