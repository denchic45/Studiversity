package com.studiversity.feature.attachment.usecase

import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class AddAttachmentUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(request: AttachmentRequest, ownerId: UUID) = transactionWorker.suspendInvoke {
        when (request) {
            is CreateFileRequest -> attachmentRepository.addFileAttachment(request, ownerId)
            is CreateLinkRequest -> attachmentRepository.addLinkAttachment(request, ownerId)
        }
    }
}