package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveCourseTopicUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) {
        courseRepository.removeTopic(courseId, topicId, relatedTopicElements)
    }
}