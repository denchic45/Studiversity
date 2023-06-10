package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseByIdUseCase @Inject constructor(
    private val courserRepository: CourseRepository,
) {
    suspend operator fun invoke(courseId: UUID): Resource<CourseResponse> {
        return courserRepository.findById(courseId)
    }
}
