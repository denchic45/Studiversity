package com.studiversity.feature.course.work.submission.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import java.util.*

class AddFileAttachmentOfSubmissionUseCase(
    private val transactionWorker: SuspendTransactionWorker,
    private val attachmentRepository: AttachmentRepository
) {
    suspend operator fun invoke(
        submissionId: UUID,
        courseId: UUID,
        workId: UUID,
        attachment: CreateFileRequest
    ): FileAttachmentHeader = transactionWorker.suspendInvoke {
        attachmentRepository.addSubmissionFileAttachment(
            submissionId = submissionId,
            courseId = courseId,
            workId = workId,
            createFileRequest = attachment
        )
    }
}