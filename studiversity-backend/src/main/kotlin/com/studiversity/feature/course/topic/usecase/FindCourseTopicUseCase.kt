package com.studiversity.feature.course.topic.usecase

import com.studiversity.feature.course.topic.CourseTopicRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseTopicUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID, topicId: UUID) = transactionWorker {
        courseTopicRepository.findById(courseId, topicId) ?: throw NotFoundException()
    }
}