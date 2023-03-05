package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import java.util.*

class UploadAttachmentToCourseWorkUseCase(
    private val courseElementRepository: CourseElementRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        attachmentRequest: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return courseElementRepository.addAttachmentToWork(courseId, workId, attachmentRequest)
    }
}