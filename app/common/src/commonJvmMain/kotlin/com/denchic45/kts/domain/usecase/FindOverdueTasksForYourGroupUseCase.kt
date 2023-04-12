package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindOverdueTasksForYourGroupUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {

    operator fun invoke(): Resource<List<CourseElementResponse>> {
        return courseElementRepository.findByStudent(
            late = true,
            statuses = SubmissionState.notSubmitted()
        )
    }
}