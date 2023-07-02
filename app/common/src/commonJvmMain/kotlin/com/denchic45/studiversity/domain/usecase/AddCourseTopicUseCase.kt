package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddCourseTopicUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseId: UUID, createTopicRequest: CreateTopicRequest) {
        courseRepository.addTopic(courseId, createTopicRequest)
    }
}