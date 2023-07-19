package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class FindCourseStudyGroupsUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: UUID) = suspendTransactionWorker {
        courseRepository.findStudyGroupsByCourse(courseId)
    }
}