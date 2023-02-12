package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UnarchiveCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: UUID) = transactionWorker {
        if (!courseRepository.exist(courseId))
            throw NotFoundException()
        courseRepository.removeArchivedCourse(courseId)
    }
}