package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseMaterialRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UpdateCourseMaterialUseCase(
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    suspend operator fun invoke(
        materialId: UUID,
        request: UpdateCourseMaterialRequest
    ): Resource<CourseMaterialResponse> {
        return courseMaterialRepository.update(materialId, request)
    }
}