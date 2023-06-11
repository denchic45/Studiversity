package com.denchic45.studiversity.feature.course.element.usecase

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