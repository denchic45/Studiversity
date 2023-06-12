package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.model.CourseResponse
import io.ktor.server.plugins.*
import java.util.*

class FindCourseByIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(id: UUID): CourseResponse = transactionWorker {
        courseRepository.findById(id) ?: throw NotFoundException()
    }
}