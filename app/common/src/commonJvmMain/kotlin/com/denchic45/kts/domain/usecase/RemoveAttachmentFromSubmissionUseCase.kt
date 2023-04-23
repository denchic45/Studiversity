package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.AttachmentRepository
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveAttachmentFromSubmissionUseCase(
    private val attachmentRepository: AttachmentRepository,
) {

    suspend operator fun invoke(attachmentId: UUID, courseId:UUID,workId:UUID,submissionId: UUID) {
        attachmentRepository.removeFromSubmission(attachmentId, courseId,workId,submissionId)
    }
}