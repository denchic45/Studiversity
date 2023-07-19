package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(id: UUID, request: UpdateCourseRequest): CourseResponse = suspendTransactionWorker {
        courseRepository.update(id, request) ?: throw NotFoundException()
    }
}