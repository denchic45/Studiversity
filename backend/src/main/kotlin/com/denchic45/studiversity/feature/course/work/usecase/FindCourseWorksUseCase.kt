package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindCourseWorksUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(authorId: UUID, late: Boolean?, submitted: Boolean?) = transactionWorker {
        courseElementRepository.findByAuthor(authorId, late, submitted)
    }
}