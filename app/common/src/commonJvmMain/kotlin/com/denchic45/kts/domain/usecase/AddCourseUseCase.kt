package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import me.tatarka.inject.annotations.Inject

@Inject
class AddCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(
        request: CreateCourseRequest
    ): Resource<CourseResponse> {
        return courseRepository.add(request)
    }
}