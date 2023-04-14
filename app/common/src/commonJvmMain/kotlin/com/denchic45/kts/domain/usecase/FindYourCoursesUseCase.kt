package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourCoursesUseCase @javax.inject.Inject constructor(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(): Resource<List<CourseResponse>> {
        return courseRepository.findByMe()
    }
}