package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.feature.course.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseByIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(id: UUID): CourseResponse = suspendTransactionWorker {
        courseRepository.findById(id) ?: throw NotFoundException()
    }
}