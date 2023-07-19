package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class ExistMemberInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
  suspend operator fun invoke(memberId: UUID, scopeId: UUID) = suspendTransactionWorker {
        userMembershipRepository.existMemberByScopeIds(memberId, listOf(scopeId))
    }
}