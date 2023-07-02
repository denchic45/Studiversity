package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddCourseMaterialUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): Resource<CourseMaterialResponse> {
        return courseElementRepository.addCourseMaterial(courseId, request)
    }

}