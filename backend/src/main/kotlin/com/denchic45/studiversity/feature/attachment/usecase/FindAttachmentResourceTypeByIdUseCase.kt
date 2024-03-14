package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindAttachmentResourceTypeByIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    operator fun invoke(attachmentId: UUID) = transactionWorker {
        attachmentRepository.findResourceTypeByAttachmentId(attachmentId)
    }
}