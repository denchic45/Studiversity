package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.updateResource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseTopicsUseCase(private val courseRepository: CourseRepository) {
    operator fun invoke(courseId: UUID): Flow<List<TopicResponse>> {
        return courseRepository.findTopicsByCourseId(courseId).filterSuccess().map { it.value }
    }
}