package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.SuspendTransactionWorker
import com.stuiversity.api.course.element.model.CreateLinkRequest
import com.stuiversity.api.course.element.model.LinkAttachmentHeader
import java.util.*

class AddLinkAttachmentOfCourseElementUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        elementId: UUID,
        attachment: CreateLinkRequest
    ): LinkAttachmentHeader {
        return transactionWorker.suspendInvoke {
            attachmentRepository.addCourseElementLinkAttachment(
                elementId = elementId,
                link = attachment
            )
        }
    }
}