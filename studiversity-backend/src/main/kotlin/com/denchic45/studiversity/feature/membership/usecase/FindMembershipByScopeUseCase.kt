package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembershipByScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val membershipRepository: MembershipRepository
) {
    operator fun invoke(scopeId: UUID, type: String?) = transactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(scopeId, type)
    }
}