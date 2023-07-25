package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseElementUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        request: UpdateCourseElementRequest,
    ): Resource<CourseElementResponse> {
        return courseElementRepository.updateCourseElement(courseId, workId, request)
    }
}