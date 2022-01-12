package com.denchic45.kts.domain

import com.denchic45.kts.data.model.domain.CourseContent
import com.denchic45.kts.data.repository.CourseRepository
import javax.inject.Inject

class RemoveCourseContentUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseContent: CourseContent) = courseRepository.removeCourseContent(courseContent)

}