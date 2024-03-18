package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.AttachmentRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveAttachmentFromSubmissionUseCase(
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(submissionId: UUID, attachmentId: UUID): EmptyResource {
        return attachmentRepository.removeFromSubmission(
            submissionId = submissionId,
            attachmentId = attachmentId
        )
    }
}