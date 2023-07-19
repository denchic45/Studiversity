package com.denchic45.studiversity.feature.course.work.submission.usecase

//class RemoveAttachmentOfSubmissionUseCase(
//    private val transactionWorker: TransactionWorker,
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