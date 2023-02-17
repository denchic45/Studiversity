package com.studiversity.feature.role.usecase

import com.denchic45.stuiversity.api.membership.model.ScopeMember
import com.studiversity.feature.membership.repository.UserMembershipRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembersInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
    operator fun invoke(scopeId: UUID): List<ScopeMember> = transactionWorker {
        userMembershipRepository.findMembersByScope(scopeId)
    }
}