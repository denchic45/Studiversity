package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseElementUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        elementId: UUID,
        request: UpdateCourseElementRequest
    ): CourseElementResponse {
        return suspendTransactionWorker {
            if (!courseElementRepository.exist(courseId, elementId)) throw NotFoundException()
            courseElementRepository.update(courseId, elementId, request)
        }
    }
}