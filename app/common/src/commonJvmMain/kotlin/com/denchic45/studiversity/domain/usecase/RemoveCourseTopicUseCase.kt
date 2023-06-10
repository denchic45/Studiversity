package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveCourseTopicUseCase @Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) {
        courseRepository.removeTopic(courseId, topicId, relatedTopicElements)
    }
}