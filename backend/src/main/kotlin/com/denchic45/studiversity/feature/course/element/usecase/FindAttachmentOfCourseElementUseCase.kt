package com.denchic45.studiversity.feature.course.element.usecase

//class FindAttachmentOfCourseElementUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, attachmentId: UUID): AttachmentResponse {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.findAttachmentByIdAndCourseElementId(courseId, elementId, attachmentId)
//                ?: throw NotFoundException()
//        }
//    }
//}