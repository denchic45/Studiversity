package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest

class AddCourseTopicUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    suspend operator fun invoke(request: CreateCourseTopicRequest) = suspendTransactionWorker {
        courseTopicRepository.add(request)
    }
}