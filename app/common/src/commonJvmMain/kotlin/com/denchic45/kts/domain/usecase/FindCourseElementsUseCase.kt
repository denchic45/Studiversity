package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseElementsUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID): Resource<Map<TopicResponse?, List<CourseElementResponse>>> {
        return courseElementRepository.findElementsByCourse(courseId)
    }
}