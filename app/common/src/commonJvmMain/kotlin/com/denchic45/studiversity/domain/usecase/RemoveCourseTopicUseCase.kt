package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) {
        courseTopicRepository.remove(courseId, topicId, relatedTopicElements)
    }
}