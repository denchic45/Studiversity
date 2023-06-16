package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class PutRolesToUserInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, roleIds: List<Long>, scopeId: UUID): Unit = transactionWorker {
        roleRepository.setByUserAndScope(userId, roleIds, scopeId)
    }
}