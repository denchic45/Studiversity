package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindYourSubmittedCourseWorksUseCase @Inject constructor(
   private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(): Resource<List<CourseElementResponse>> {
        return courseElementRepository.findByStudent(statuses = listOf(SubmissionState.SUBMITTED))
    }
}