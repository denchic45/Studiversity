package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseTopicUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID, topicId: UUID, updateTopicRequest: UpdateTopicRequest) = transactionWorker {
        courseTopicRepository.update(courseId, topicId, updateTopicRequest) ?: throw NotFoundException()
    }
}