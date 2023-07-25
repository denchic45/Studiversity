package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseWorkRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourSubmittedCourseWorksUseCase(
    private val courseWorkRepository: CourseWorkRepository
) {

    operator fun invoke(): Flow<Resource<List<CourseWorkResponse>>> {
        return courseWorkRepository.findSubmittedByYourAuthor()
    }
}