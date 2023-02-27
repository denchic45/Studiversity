package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.stuiversity.api.course.model.CourseResponse
import javax.inject.Inject

class FindCourseByContainsNameUseCase @Inject constructor(
    courseRepository: CourseRepository,
) : FindByContainsNameUseCase<CourseResponse>(courseRepository)