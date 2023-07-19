package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class FindMembershipByScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val membershipRepository: MembershipRepository
) {
  suspend operator fun invoke(scopeId: UUID, type: String?) = suspendTransactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(scopeId, type)
    }
}