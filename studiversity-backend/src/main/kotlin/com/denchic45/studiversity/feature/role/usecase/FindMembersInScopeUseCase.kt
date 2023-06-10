package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.stuiversity.api.membership.model.ScopeMember
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindMembersInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val userMembershipRepository: UserMembershipRepository
) {
    operator fun invoke(scopeId: UUID): List<ScopeMember> = transactionWorker {
        userMembershipRepository.findMembersByScope(scopeId)
    }
}