package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseElementsUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
    ): Resource<List<Pair<TopicResponse?, List<CourseElementResponse>>>> {
        return courseElementRepository.findElementsByCourse(courseId)
    }
}