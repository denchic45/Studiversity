package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, createCourseWorkRequest: CreateCourseWorkRequest): Resource<CourseElementResponse> {
       return courseElementRepository.addCourseWork(courseId, createCourseWorkRequest)
    }

}