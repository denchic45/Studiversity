package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class RemoveRoleFromUserInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roleRepository: RoleRepository
) {
  suspend operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID) = suspendTransactionWorker {
        roleRepository.removeRoleByUserAndScope(userId, roleId, scopeId)
    }
}