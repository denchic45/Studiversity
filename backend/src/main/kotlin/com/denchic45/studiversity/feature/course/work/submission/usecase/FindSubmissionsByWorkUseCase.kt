package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class FindSubmissionsByWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val submissionRepository: SubmissionRepository,
    private val roleRepository: RoleRepository
) {

    suspend operator fun invoke(courseId: UUID, courseWorkId: UUID) = suspendTransactionWorker {
        submissionRepository.findByWorkId(
            courseWorkId,
            roleRepository.findUsersIdsByScopeIdAndRoleId(courseId, Role.Student.id)
        )
    }
}