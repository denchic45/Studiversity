package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(id: UUID, request: UpdateCourseRequest): CourseResponse = transactionWorker {
        courseRepository.update(id, request) ?: throw NotFoundException()
    }
}