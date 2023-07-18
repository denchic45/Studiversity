package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ObserveCourseTopicsUseCase(private val courseTopicRepository: CourseTopicRepository) {
    operator fun invoke(courseId: UUID): Flow<Resource<List<TopicResponse>>> {
        return courseTopicRepository.observeByCourseId(courseId)
    }
}