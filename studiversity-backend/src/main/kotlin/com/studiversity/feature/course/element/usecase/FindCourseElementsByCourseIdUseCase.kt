package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.course.element.model.SortingCourseElements
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

class FindCourseElementsByCourseIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(courseId: UUID, sorting: List<SortingCourseElements>?) = transactionWorker {
        courseElementRepository.findElementsByCourseId(courseId, sorting)
    }
}