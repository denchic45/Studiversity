package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        courseId: UUID,
        request: UpdateCourseRequest
    ): Resource<CourseResponse> {
        return courseRepository.update(courseId, request)
    }
}