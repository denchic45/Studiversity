package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, workId: UUID, ): Resource<CourseWorkResponse> {
        return courseElementRepository.findWorkById(courseId, workId)
    }
}