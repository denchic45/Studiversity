package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class RemoveRoleFromUserInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID) = transactionWorker {
        roleRepository.removeByUserAndScope(userId, roleId, scopeId)
    }
}