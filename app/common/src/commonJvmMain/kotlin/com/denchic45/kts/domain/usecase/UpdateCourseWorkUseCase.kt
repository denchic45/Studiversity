package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import java.util.*
import javax.inject.Inject

class UpdateCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ) {
        courseElementRepository.updateWork(courseId, workId, updateCourseWorkRequest)
    }
}