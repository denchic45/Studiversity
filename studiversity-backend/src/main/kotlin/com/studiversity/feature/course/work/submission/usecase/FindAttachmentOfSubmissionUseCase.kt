package com.studiversity.feature.course.work.submission.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.Attachment
import com.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindAttachmentOfSubmissionUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID): Attachment {
        return transactionWorker.suspendInvoke {
            attachmentRepository.findAttachmentByIdAndSubmissionId(courseId, elementId, submissionId, attachmentId)
                ?: throw NotFoundException()
        }
    }
}