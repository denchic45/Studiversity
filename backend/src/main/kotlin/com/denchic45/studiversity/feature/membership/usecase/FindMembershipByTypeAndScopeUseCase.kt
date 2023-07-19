package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class FindMembershipByTypeAndScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val membershipRepository: MembershipRepository
) {

  suspend operator fun invoke(type: String, scopeId: UUID) = suspendTransactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(type, scopeId)
    }
}