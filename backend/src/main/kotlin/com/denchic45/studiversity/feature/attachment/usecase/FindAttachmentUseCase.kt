package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
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