package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class DetachStudyGroupToCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(studyGroupId: UUID, courseId: UUID) = transactionWorker {
        courseRepository.removeStudyGroupFromCourse(courseId, studyGroupId)
    }
}