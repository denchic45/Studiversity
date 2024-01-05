package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.CourseErrors
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class AttachStudyGroupToCourseUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: UUID, studyGroupId: UUID) = suspendTransactionWorker {
        if (courseRepository.existStudyGroupByCourse(courseId, studyGroupId))
            throw BadRequestException(CourseErrors.STUDY_GROUP_ALREADY_EXIST)
        courseRepository.addStudyGroupToCourse(courseId, studyGroupId)
    }
}