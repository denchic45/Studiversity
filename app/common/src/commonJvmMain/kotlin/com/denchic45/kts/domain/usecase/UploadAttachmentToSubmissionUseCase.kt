package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.AttachmentRepository
import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UploadAttachmentToSubmissionUseCase @javax.inject.Inject constructor(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        courseId: UUID,
        workId: UUID,
        submissionId:UUID,
        attachmentRequest: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return attachmentRepository.addAttachmentToSubmission(courseId, workId,submissionId, attachmentRequest)
    }
}