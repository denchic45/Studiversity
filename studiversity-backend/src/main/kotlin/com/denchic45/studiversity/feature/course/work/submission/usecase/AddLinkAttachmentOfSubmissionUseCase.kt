package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import java.util.*

//class AddLinkAttachmentOfSubmissionUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    operator fun invoke(submissionId: UUID, attachment: CreateLinkRequest) = transactionWorker {
//        attachmentRepository.addSubmissionLinkAttachment(submissionId, attachment)
//    }
//}