package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembershipByTypeAndScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val membershipRepository: MembershipRepository
) {

    operator fun invoke(type: String, scopeId: UUID) = transactionWorker {
        membershipRepository.findMembershipIdByTypeAndScopeId(type, scopeId)
    }
}