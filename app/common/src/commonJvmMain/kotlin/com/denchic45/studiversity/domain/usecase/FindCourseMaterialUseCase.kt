package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseMaterialRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCourseMaterialUseCase(
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        materialId: UUID,
    ): Resource<CourseMaterialResponse> {
        return courseMaterialRepository.findById(courseId, materialId)
    }
}