package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseTopicUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID, updateTopicRequest: UpdateTopicRequest,
    ): Resource<TopicResponse> {
        return courseRepository.updateTopic(courseId, topicId, updateTopicRequest)
    }
}