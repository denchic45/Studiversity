package com.studiversity.feature.attachment.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.TransactionWorker
import java.util.UUID

class CheckOwnerAttachmentUseCase(private val transactionWorker: TransactionWorker,
                                  private val attachmentRepository: AttachmentRepository) {
operator fun invoke(ownerId:UUID, attachmentId:UUID) = transactionWorker {
    attachmentRepository.checkIsOwner(ownerId,attachmentId)
}
}