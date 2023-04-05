package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class ObserveCourseTopicsUseCase @javax.inject.Inject constructor(private val courseRepository: CourseRepository) {
    operator fun invoke(courseId: UUID): Flow<Resource<List<TopicResponse>>> {
        return courseRepository.observeTopicsByCourseId(courseId)
    }
}