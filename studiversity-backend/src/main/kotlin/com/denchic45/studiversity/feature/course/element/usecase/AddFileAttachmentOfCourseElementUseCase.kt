package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import java.util.*

//class AddFileAttachmentOfCourseElementUseCase(
//    private val transactionWorker: SuspendTransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(
//        elementId: UUID,
//        courseId: UUID,
//        attachment: CreateFileRequest
//    ): FileAttachmentHeader {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.addCourseElementFileAttachment(
//                elementId = elementId,
//                courseId = courseId,
//                file = attachment
//            )
//        }
//    }
//}