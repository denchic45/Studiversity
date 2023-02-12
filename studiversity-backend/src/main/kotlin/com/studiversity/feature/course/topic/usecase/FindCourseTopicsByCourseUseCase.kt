package com.studiversity.feature.course.topic.usecase

import com.studiversity.feature.course.topic.CourseTopicRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindCourseTopicsByCourseUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseTopicRepository: CourseTopicRepository
) {

    operator fun invoke(courseId: UUID) = transactionWorker {
        courseTopicRepository.findByCourseId(courseId)
    }
}