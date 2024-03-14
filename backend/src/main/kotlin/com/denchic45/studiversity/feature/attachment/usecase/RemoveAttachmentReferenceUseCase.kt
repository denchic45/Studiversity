package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*


class RemoveAttachmentReferenceUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(attachmentId: UUID, consumerId: UUID) = suspendTransactionWorker.invoke {
        attachmentRepository.removeReferenceByConsumer(attachmentId, consumerId)
    }
}