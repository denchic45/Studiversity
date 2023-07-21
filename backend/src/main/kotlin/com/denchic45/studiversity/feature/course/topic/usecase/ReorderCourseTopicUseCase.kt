package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.ReorderCourseTopicRequest
import io.ktor.server.plugins.*
import java.util.*

class ReorderCourseTopicUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    suspend operator fun invoke(topicId: UUID, request: ReorderCourseTopicRequest) = suspendTransactionWorker {
        courseTopicRepository.reorder(topicId, request.order) ?: throw NotFoundException()
    }
}