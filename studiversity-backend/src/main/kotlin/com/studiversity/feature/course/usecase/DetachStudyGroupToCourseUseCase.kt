package com.studiversity.feature.course.usecase

import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class DetachStudyGroupToCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(studyGroupId: UUID, courseId: UUID) = transactionWorker {
        courseRepository.removeStudyGroupFromCourse(courseId, studyGroupId)
    }
}