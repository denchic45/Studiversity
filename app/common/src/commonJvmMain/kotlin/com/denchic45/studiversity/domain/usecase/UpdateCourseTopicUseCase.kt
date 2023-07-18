package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID, updateTopicRequest: UpdateTopicRequest,
    ): Resource<TopicResponse> {
        return courseTopicRepository.update(courseId, topicId, updateTopicRequest)
    }
}