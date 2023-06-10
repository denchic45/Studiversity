package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UpdateCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ): Resource<CourseWorkResponse> {
        return courseElementRepository.updateWork(courseId, workId, updateCourseWorkRequest)
    }
}