package com.studiversity.feature.attachment.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.SuspendTransactionWorker
import java.util.*


class RemoveAttachmentReferenceUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(attachmentId: UUID, consumerId: UUID) = transactionWorker.suspendInvoke {
        attachmentRepository.removeConsumer(attachmentId, consumerId)
    }
}