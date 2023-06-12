package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository
) {
    operator fun invoke(courseId: UUID, workId: UUID, request: UpdateCourseWorkRequest) = transactionWorker {
        courseElementRepository.update(courseId, workId, request) ?: throw NotFoundException()
    }
}