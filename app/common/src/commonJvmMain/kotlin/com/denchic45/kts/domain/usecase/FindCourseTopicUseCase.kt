package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseTopicUseCase @javax.inject.Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseId: UUID, topicId: UUID): Flow<TopicResponse> {
        return courseRepository.findTopic(courseId, topicId).filterSuccess().map { it.value }
    }
}