package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
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