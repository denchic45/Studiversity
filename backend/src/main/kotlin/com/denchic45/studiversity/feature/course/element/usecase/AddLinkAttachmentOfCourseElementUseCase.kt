package com.denchic45.studiversity.feature.course.element.usecase

//class AddLinkAttachmentOfCourseElementUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(
//        elementId: UUID,
//        attachment: CreateLinkRequest
//    ): LinkAttachmentHeader {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.addCourseElementLinkAttachment(
//                elementId = elementId,
//                link = attachment
//            )
//        }
//    }
//}