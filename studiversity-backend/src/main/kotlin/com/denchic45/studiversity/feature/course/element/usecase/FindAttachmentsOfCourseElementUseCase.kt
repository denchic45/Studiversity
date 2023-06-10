package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

//class FindAttachmentsOfCourseElementUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    operator fun invoke(elementId: UUID) = transactionWorker {
//        attachmentRepository.findAttachmentsByCourseElementId(elementId)
//    }
//}