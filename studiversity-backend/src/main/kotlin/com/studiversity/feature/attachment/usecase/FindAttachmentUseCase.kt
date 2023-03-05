package com.studiversity.feature.attachment.usecase

import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindAttachmentUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(attachmentId: UUID): AttachmentResponse {
        return transactionWorker.suspendInvoke {
            attachmentRepository.findAttachmentById(attachmentId)
                ?: throw NotFoundException()
        }
    }
}