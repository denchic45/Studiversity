package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class FindCourseTopicsByCourseUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    suspend operator fun invoke(courseId: UUID) = suspendTransactionWorker {
        courseTopicRepository.findByCourseId(courseId)
    }
}