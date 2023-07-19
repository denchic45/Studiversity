package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class ArchiveCourseUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: UUID) = suspendTransactionWorker {
        if (!courseRepository.exist(courseId))
            throw NotFoundException()
        courseRepository.addArchivedCourse(courseId)
    }
}