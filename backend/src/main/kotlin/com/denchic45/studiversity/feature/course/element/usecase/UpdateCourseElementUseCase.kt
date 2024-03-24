package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import java.util.*

class UpdateCourseElementUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(
        elementId: UUID,
        request: UpdateCourseElementRequest
    ): CourseElementResponse {
        return suspendTransactionWorker {
            courseElementRepository.update(elementId, request)
        }
    }
}