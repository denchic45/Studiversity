package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseMaterialRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseMaterialUseCase(
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    suspend operator fun invoke(materialId: UUID): Resource<CourseMaterialResponse> {
        return courseMaterialRepository.findById(materialId)
    }
}