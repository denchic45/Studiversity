package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class CheckOwnerAttachmentUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(ownerId: UUID, attachmentId: UUID) = suspendTransactionWorker {
        attachmentRepository.checkIsOwner(ownerId, attachmentId)
    }
}