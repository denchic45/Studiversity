package com.studiversity.feature.course.element.usecase

import com.studiversity.feature.attachment.AttachmentRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

//class FindAttachmentsOfCourseElementUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    operator fun invoke(elementId: UUID) = transactionWorker {
//        attachmentRepository.findAttachmentsByCourseElementId(elementId)
//    }
//}