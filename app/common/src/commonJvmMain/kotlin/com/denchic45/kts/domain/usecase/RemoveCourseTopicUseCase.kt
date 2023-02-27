package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import java.util.*
import javax.inject.Inject

class RemoveCourseTopicUseCase @Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) {
        courseRepository.removeTopic(courseId, topicId, relatedTopicElements)
    }
}