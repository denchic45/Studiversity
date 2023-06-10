package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, createCourseWorkRequest: CreateCourseWorkRequest): Resource<CourseWorkResponse> {
       return courseElementRepository.addCourseWork(courseId, createCourseWorkRequest)
    }

}