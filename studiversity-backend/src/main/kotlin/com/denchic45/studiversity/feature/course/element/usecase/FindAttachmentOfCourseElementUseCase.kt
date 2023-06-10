package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

//class FindAttachmentOfCourseElementUseCase(
//    private val transactionWorker: SuspendTransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, attachmentId: UUID): AttachmentResponse {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.findAttachmentByIdAndCourseElementId(courseId, elementId, attachmentId)
//                ?: throw NotFoundException()
//        }
//    }
//}