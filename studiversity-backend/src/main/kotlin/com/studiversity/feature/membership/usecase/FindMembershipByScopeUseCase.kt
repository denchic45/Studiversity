package com.studiversity.feature.membership.usecase

import com.studiversity.feature.membership.repository.MembershipRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembershipByScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val membershipRepository: MembershipRepository
) {
    operator fun invoke(scopeId: UUID, type: String?) = transactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(scopeId, type)
    }
}