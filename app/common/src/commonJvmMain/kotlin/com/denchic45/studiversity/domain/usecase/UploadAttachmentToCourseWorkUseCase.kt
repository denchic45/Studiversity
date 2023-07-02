package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UploadAttachmentToCourseWorkUseCase(
    private val courseElementRepository: CourseElementRepository,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return courseElementRepository.addAttachmentToWork(courseId, workId, request)
    }
}