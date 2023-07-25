package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseMaterialRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddCourseMaterialUseCase(
    private val courseMaterialRepository: CourseMaterialRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): Resource<CourseMaterialResponse> {
        return courseMaterialRepository.add(courseId, request)
    }

}