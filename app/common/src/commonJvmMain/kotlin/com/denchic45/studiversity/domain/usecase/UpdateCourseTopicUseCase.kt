package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UpdateCourseTopicUseCase @Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        topicId: UUID, updateTopicRequest: UpdateTopicRequest,
    ): Resource<TopicResponse> {
        return courseRepository.updateTopic(courseId, topicId, updateTopicRequest)
    }
}