package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import java.util.*

class FindCourseElementsByCourseIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(courseId: UUID, sorting: List<CourseElementsSorting>?) = suspendTransactionWorker {
        courseElementRepository.findElementsByCourseId(courseId, sorting)
    }
}