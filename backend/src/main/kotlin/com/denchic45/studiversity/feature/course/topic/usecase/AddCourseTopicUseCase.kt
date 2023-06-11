package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import java.util.*

class AddCourseTopicUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID, createTopicRequest: CreateTopicRequest) = transactionWorker {
        courseTopicRepository.add(courseId, createTopicRequest)
    }
}