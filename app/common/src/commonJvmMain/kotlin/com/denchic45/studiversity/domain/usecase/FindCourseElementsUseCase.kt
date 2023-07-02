package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseElementsUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
    ): Resource<List<Pair<TopicResponse?, List<CourseElementResponse>>>> {
        return courseElementRepository.findElementsByCourse(courseId)
    }
}