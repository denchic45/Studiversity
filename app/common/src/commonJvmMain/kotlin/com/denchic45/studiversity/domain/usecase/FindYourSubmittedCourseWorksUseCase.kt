package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourSubmittedCourseWorksUseCase(
    private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(): Flow<Resource<List<CourseWorkResponse>>> {
        return courseElementRepository.findSubmittedByYourAuthor()
    }
}