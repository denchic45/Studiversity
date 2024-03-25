package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindSubmissionUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository
) {

  suspend operator fun invoke(submissionId: UUID) = suspendTransactionWorker {
        submissionRepository.find(submissionId) ?: throw NotFoundException()
    }
}