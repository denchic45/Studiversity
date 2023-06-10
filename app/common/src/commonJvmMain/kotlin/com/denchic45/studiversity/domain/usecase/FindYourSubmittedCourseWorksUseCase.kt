package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.model.Task
import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindYourSubmittedCourseWorksUseCase @Inject constructor(
   private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(): Resource<List<CourseElementResponse>> {
        return courseElementRepository.findByStudent(statuses = listOf(SubmissionState.SUBMITTED))
    }
}