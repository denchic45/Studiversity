package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(courseId: UUID, request: CreateCourseTopicRequest) {
        courseTopicRepository.add(courseId, request)
    }
}