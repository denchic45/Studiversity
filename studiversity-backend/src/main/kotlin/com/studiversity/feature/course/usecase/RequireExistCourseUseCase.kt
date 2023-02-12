package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RequireExistCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {

    operator fun invoke(id: UUID) = transactionWorker {
        if (!courseRepository.exist(id)) throw NotFoundException()
    }
}