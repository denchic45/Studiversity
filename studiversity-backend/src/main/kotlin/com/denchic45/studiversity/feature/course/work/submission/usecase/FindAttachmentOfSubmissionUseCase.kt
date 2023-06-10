package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

//class FindAttachmentOfSubmissionUseCase(
//    private val transactionWorker: SuspendTransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID): AttachmentResponse {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.findAttachmentByIdAndReferenceId(courseId, elementId, submissionId, attachmentId)
//                ?: throw NotFoundException()
//        }
//    }
//}