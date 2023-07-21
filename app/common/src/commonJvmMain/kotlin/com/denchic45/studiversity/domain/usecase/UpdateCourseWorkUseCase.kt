package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseWorkRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        request: UpdateCourseWorkRequest,
    ): Resource<CourseWorkResponse> {
        return courseWorkRepository.update(courseId, workId, request)
    }
}