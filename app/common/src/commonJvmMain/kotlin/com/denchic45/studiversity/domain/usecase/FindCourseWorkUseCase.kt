package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseWorkRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
) {
    suspend operator fun invoke(workId: UUID): Resource<CourseWorkResponse> {
        return courseWorkRepository.findById(workId)
    }
}