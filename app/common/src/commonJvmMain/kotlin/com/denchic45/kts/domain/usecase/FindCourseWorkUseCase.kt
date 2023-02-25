package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import java.util.*
import javax.inject.Inject

class FindCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
    ): Resource<CourseElementResponse> = courseElementRepository.findById(courseId, workId)
}