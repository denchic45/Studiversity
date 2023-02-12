package com.studiversity.feature.membership.usecase

import com.studiversity.feature.membership.repository.MembershipRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembershipByTypeAndScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val membershipRepository: MembershipRepository
) {

    operator fun invoke(type: String, scopeId: UUID) = transactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(type, scopeId)
    }
}