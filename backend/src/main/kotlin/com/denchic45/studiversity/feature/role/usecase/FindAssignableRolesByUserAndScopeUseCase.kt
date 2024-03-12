package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.UUID

class FindAssignableRolesByUserAndScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId:UUID) = transactionWorker {
        roleRepository.findAssignableRolesByUserAndScope(userId,scopeId)
    }
}
