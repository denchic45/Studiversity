package com.denchic45.studiversity.feature.course.element.usecase

import com.denchic45.studiversity.feature.attachment.AttachmentRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

//class RemoveAttachmentOfCourseElementUseCase(
//    private val transactionWorker: SuspendTransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, attachmentId: UUID) = transactionWorker.suspendInvoke {
//        if (!attachmentRepository.removeByCourseElementId(courseId, elementId, attachmentId))
//            throw NotFoundException()
//    }
//}