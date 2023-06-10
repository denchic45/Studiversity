package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

//class RemoveAttachmentOfSubmissionUseCase(
//    private val transactionWorker: SuspendTransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID) {
//        transactionWorker.suspendInvoke {
//            if (!attachmentRepository.removeBySubmissionId(courseId, elementId, submissionId, attachmentId))
//                throw NotFoundException()
//        }
//    }
//}