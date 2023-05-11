package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddCourseTopicUseCase @Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseId: UUID, createTopicRequest: CreateTopicRequest) {
        courseRepository.addTopic(courseId, createTopicRequest)
    }
}