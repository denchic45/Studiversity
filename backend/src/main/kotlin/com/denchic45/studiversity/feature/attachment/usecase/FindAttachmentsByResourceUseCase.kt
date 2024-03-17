package com.denchic45.studiversity.feature.attachment.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import java.util.*

class FindAttachmentsByResourceUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(resourceId: UUID): List<AttachmentHeader> {
        return suspendTransactionWorker.invoke {
            attachmentRepository.findAttachmentsByResourceId(resourceId)
        }
    }
}