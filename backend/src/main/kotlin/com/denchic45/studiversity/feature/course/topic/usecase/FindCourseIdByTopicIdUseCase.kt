package com.denchic45.studiversity.feature.course.topic.usecase

import com.denchic45.studiversity.feature.course.topic.CourseTopicRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindCourseIdByTopicIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID) = transactionWorker {
        courseTopicRepository.findCourseId(courseId) ?: throw NotFoundException()
    }
}