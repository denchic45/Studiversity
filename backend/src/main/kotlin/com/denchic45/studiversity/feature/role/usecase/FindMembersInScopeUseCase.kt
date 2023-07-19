package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import java.util.*

class FindMembersInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
  suspend operator fun invoke(scopeId: UUID): List<ScopeMember> = suspendTransactionWorker {
        userMembershipRepository.findMembersByScope(scopeId)
    }
}