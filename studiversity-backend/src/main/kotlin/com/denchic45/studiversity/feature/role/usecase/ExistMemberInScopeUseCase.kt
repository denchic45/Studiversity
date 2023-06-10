package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class ExistMemberInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
    operator fun invoke(memberId: UUID, scopeId: UUID) = transactionWorker {
        userMembershipRepository.existMemberByScopeIds(memberId, listOf(scopeId))
    }
}