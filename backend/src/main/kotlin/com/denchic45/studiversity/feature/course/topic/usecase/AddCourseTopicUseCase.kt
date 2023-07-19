package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import java.util.*

class AddCourseTopicUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    suspend operator fun invoke(courseId: UUID, createTopicRequest: CreateTopicRequest) = suspendTransactionWorker {
        courseTopicRepository.add(courseId, createTopicRequest)
    }
}