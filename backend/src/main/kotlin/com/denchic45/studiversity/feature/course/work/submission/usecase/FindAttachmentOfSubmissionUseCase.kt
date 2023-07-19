package com.denchic45.studiversity.feature.course.work.submission.usecase

//class FindAttachmentOfSubmissionUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID): AttachmentResponse {
//        return transactionWorker.suspendInvoke {
//            attachmentRepository.findAttachmentByIdAndReferenceId(courseId, elementId, submissionId, attachmentId)
//                ?: throw NotFoundException()
//        }
//    }
//}