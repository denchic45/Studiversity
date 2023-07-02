package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateCourseMaterialUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ): Resource<CourseMaterialResponse> {
        return courseElementRepository.updateCourseMaterial(courseId, materialId, request)
    }
}