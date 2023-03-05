package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import java.util.*
import javax.inject.Inject

class UpdateCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ): Resource<CourseElementResponse> {
      return  courseElementRepository.updateWork(courseId, workId, updateCourseWorkRequest)
    }
}