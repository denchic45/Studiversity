package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(courseId: UUID, workId: UUID, request: UpdateCourseWorkRequest) = transactionWorker {
        courseWorkRepository.update(courseId, workId, request) ?: throw NotFoundException()
    }
}