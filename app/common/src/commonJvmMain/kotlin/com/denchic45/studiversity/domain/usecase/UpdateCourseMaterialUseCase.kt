package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UpdateCourseMaterialUseCase @Inject constructor(
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