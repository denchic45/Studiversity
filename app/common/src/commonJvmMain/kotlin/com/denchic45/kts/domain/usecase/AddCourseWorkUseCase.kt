package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import java.util.*
import javax.inject.Inject

class AddCourseWorkUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, createCourseWorkRequest: CreateCourseWorkRequest,attachments: List<Attachment>) {
        courseElementRepository.addCourseWork(courseId, createCourseWorkRequest,attachments)
    }

}