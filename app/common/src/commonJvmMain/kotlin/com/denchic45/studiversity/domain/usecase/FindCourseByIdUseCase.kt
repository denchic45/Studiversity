package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseByIdUseCase(
    private val courserRepository: CourseRepository,
) {
    suspend operator fun invoke(courseId: UUID): Resource<CourseResponse> {
        return courserRepository.findById(courseId)
    }
}
