package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseWorkRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
) {
    suspend operator fun invoke(courseId: UUID, workId: UUID): Resource<CourseWorkResponse> {
        return courseWorkRepository.findById(courseId, workId)
    }
}