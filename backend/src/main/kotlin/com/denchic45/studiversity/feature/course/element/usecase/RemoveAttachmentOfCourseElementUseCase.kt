package com.denchic45.studiversity.feature.course.element.usecase

//class RemoveAttachmentOfCourseElementUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, attachmentId: UUID) = transactionWorker.suspendInvoke {
//        if (!attachmentRepository.removeByCourseElementId(courseId, elementId, attachmentId))
//            throw NotFoundException()
//    }
//}