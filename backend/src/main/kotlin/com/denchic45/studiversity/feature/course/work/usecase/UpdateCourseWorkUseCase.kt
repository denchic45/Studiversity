package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseWorkRepository: CourseWorkRepository
) {
    suspend operator fun invoke(workId: UUID, request: UpdateCourseWorkRequest) = suspendTransactionWorker {
        courseWorkRepository.update(workId, request) ?: throw NotFoundException()
    }
}