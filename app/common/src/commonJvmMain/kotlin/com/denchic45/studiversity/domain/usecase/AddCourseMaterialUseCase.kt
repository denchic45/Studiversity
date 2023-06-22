package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddCourseMaterialUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, request: CreateCourseMaterialRequest): Resource<CourseMaterialResponse> {
       return courseElementRepository.addCourseMaterial(courseId, request)
    }

}