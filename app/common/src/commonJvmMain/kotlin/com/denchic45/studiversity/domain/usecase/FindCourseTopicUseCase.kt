package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.studiversity.domain.filterSuccess
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(courseId: UUID, topicId: UUID): Flow<TopicResponse> {
        return courseTopicRepository.findById(courseId, topicId).filterSuccess().map { it.value }
    }
}