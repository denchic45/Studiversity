package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(
        topicId: UUID,
        request: UpdateCourseTopicRequest,
    ): Resource<CourseTopicResponse> {
        return courseTopicRepository.update(topicId, request)
    }
}