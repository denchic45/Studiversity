package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindCourseByContainsNameUseCase(
    courseRepository: CourseRepository,
) : FindByContainsNameUseCase<CourseResponse>(courseRepository)