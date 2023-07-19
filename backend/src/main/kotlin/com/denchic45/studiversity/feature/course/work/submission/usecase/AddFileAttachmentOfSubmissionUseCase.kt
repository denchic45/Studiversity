package com.denchic45.studiversity.feature.course.work.submission.usecase

//class AddFileAttachmentOfSubmissionUseCase(
//    private val transactionWorker: TransactionWorker,
//    private val attachmentRepository: AttachmentRepository
//) {
//    suspend operator fun invoke(
//        submissionId: UUID,
//        courseId: UUID,
//        workId: UUID,
//        attachment: CreateFileRequest
//    ): FileAttachmentHeader = transactionWorker.suspendInvoke {
//        attachmentRepository.addSubmissionFileAttachment(
//            submissionId = submissionId,
//            courseId = courseId,
//            workId = workId,
//            createFileRequest = attachment
//        )
//    }
//}