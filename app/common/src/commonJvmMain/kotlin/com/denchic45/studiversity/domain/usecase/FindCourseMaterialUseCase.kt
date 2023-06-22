package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindCourseMaterialUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        materialId: UUID,
    ): Resource<CourseMaterialResponse> {
        return courseElementRepository.findMaterialById(courseId, materialId)
    }
}