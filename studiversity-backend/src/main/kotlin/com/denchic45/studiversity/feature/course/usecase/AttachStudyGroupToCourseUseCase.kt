package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.CourseErrors
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class AttachStudyGroupToCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: UUID, studyGroupId: UUID) = transactionWorker {
        if (courseRepository.existStudyGroupByCourse(courseId, studyGroupId))
            throw BadRequestException(CourseErrors.STUDY_GROUP_ALREADY_EXIST)
        courseRepository.addCourseStudyGroup(courseId, studyGroupId)
    }
}