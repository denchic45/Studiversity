package com.studiversity.feature.course.topic.usecase

import com.studiversity.feature.course.topic.CourseTopicRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.course.topic.model.CreateTopicRequest
import java.util.*

class AddCourseTopicUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID, createTopicRequest: CreateTopicRequest) = transactionWorker {
        courseTopicRepository.add(courseId, createTopicRequest)
    }
}