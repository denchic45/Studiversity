package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.model.CourseResponse
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseByContainsNameUseCase @Inject constructor(
    courseRepository: CourseRepository,
) : FindByContainsNameUseCase<CourseResponse>(courseRepository)