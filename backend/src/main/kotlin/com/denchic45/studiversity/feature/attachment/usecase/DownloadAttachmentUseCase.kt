package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentErrors
import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class DownloadAttachmentUseCase(
    private val transactionWorker: TransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    operator fun invoke(attachmentId: UUID) = transactionWorker {
        if (!attachmentRepository.isFileExists(attachmentId))
            throw NotFoundException(AttachmentErrors.FILE_ATTACHMENT_NOT_FOUND)
        attachmentRepository.findFileSource(attachmentId)
    }
}
