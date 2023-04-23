package com.studiversity.feature.course.work.submission.usecase

import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindAttachmentsByReferenceUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(consumerId:UUID): List<AttachmentHeader> {
        return transactionWorker.suspendInvoke {
            attachmentRepository.findAttachmentsByReferenceId(consumerId)
        }
    }
}