package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindCourseStudyGroupsUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: UUID) = transactionWorker {
        courseRepository.findStudyGroupsByCourse(courseId)
    }
}