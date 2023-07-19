package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseTopicUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    suspend operator fun invoke(courseId: UUID, topicId: UUID) = suspendTransactionWorker {
        courseTopicRepository.findById(courseId, topicId) ?: throw NotFoundException()
    }
}