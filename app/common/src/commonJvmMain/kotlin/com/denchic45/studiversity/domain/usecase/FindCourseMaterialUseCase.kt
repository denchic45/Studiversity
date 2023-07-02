package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseMaterialUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        materialId: UUID,
    ): Resource<CourseMaterialResponse> {
        return courseElementRepository.findMaterialById(courseId, materialId)
    }
}