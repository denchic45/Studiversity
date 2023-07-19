package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class FindSubmissionsByWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userMembershipRepository: UserMembershipRepository,
    private val submissionRepository: SubmissionRepository
) {

  suspend operator fun invoke(courseId: UUID, courseWorkId: UUID) = suspendTransactionWorker {
        submissionRepository.findByWorkId(
            courseId,
            courseWorkId,
            userMembershipRepository.findMemberIdsByScopeAndRole(courseId, Role.Student.id)
        )
    }
}