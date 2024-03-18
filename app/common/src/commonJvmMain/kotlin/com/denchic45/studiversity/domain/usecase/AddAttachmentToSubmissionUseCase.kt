package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class AddAttachmentToSubmissionUseCase(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        submissionId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> {
        return attachmentRepository.addAttachmentToSubmission(submissionId, request)
    }
}