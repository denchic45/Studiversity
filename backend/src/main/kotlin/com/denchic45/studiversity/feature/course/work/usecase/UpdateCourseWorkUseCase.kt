package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseWorkRepository: CourseWorkRepository
) {
  suspend operator fun invoke(courseId: UUID, workId: UUID, request: UpdateCourseWorkRequest) = suspendTransactionWorker {
        courseWorkRepository.update(courseId, workId, request) ?: throw NotFoundException()
    }
}