package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import io.ktor.server.plugins.*
import java.util.*

class UpdateCourseTopicUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

  suspend operator fun invoke(courseId: UUID, topicId: UUID, request: UpdateTopicRequest) = suspendTransactionWorker {
        courseTopicRepository.update(courseId, topicId, request) ?: throw NotFoundException()
    }
}