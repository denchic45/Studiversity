package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class RequireExistsCourseElementUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(elementId: UUID) = transactionWorker {
        if (!courseElementRepository.isExists(elementId)) TODO("Ошибка из-за отсутствия элемента курса")
    }
}