package com.studiversity.feature.attachment.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.SuspendTransactionWorker
import java.util.*


class RemoveAttachmentUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(attachmentId: UUID) = transactionWorker.suspendInvoke {
        attachmentRepository.removeAttachment(attachmentId)
    }
}