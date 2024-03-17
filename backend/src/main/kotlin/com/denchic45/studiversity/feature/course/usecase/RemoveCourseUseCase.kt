package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.CourseErrors
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.ktor.ConflictException
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository,
    private val scopeRepository: ScopeRepository
) {
    operator fun invoke(courseId: UUID) = transactionWorker {
        if (!courseRepository.exist(courseId)) throw NotFoundException()
        if (!courseRepository.isArchivedCourse(courseId)) throw ConflictException(CourseErrors.COURSE_IS_NOT_ARCHIVED)

        courseRepository.removeCourse(courseId)
        scopeRepository.remove(courseId)
    }
}