package com.studiversity.feature.role.usecase

import com.studiversity.feature.membership.repository.UserMembershipRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class ExistMemberInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
    operator fun invoke(memberId: UUID, scopeId: UUID) = transactionWorker {
        userMembershipRepository.existMemberByScopeIds(memberId, listOf(scopeId))
    }
}