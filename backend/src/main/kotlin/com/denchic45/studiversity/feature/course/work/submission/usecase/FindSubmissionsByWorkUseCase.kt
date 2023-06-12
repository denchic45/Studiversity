package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class FindSubmissionsByWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val userMembershipRepository: UserMembershipRepository,
    private val submissionRepository: SubmissionRepository
) {

    operator fun invoke(courseId: UUID, courseWorkId: UUID) = transactionWorker {
        submissionRepository.findByWorkId(
            courseId,
            courseWorkId,
            userMembershipRepository.findMemberIdsByScopeAndRole(courseId, Role.Student.id)
        )
    }
}