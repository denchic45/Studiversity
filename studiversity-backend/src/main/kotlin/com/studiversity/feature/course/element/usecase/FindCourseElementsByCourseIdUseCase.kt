package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import java.util.*

class FindCourseElementsByCourseIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(courseId: UUID, sorting: List<CourseElementsSorting>?) = transactionWorker {
        courseElementRepository.findElementsByCourseId(courseId, sorting)
    }
}