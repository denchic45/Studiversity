package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.element.model.UploadedAttachmentRequest
import java.util.*

class AddAttachmentUseCase(
    private val transactionWorker: TransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    operator fun invoke(request: AttachmentRequest, resourceId: UUID) = transactionWorker.invoke {
        when (request) {
            is CreateFileRequest -> attachmentRepository.addFileAttachment(request, resourceId)
            is CreateLinkRequest -> attachmentRepository.addLinkAttachment(request, resourceId)
            is UploadedAttachmentRequest -> {
                attachmentRepository.addAttachmentReference(request.attachmentId, resourceId)
            }
        }
    }
}