package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseElementUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(
        courseId: UUID,
        elementId: UUID,
        updateCourseElementRequest: UpdateCourseElementRequest
    ): CourseElementResponse {
        return transactionWorker {
            if (!courseElementRepository.exist(courseId, elementId)) throw NotFoundException()
            courseElementRepository.update(courseId, elementId, updateCourseElementRequest)
        }
    }
}