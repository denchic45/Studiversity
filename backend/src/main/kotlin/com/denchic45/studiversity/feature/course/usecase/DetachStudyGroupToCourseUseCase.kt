package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class DetachStudyGroupToCourseUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(studyGroupId: UUID, courseId: UUID) = suspendTransactionWorker {
        courseRepository.removeStudyGroupFromCourse(courseId, studyGroupId)
    }
}