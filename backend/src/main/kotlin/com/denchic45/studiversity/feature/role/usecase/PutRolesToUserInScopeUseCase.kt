package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class PutRolesToUserInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roleRepository: RoleRepository
) {
  suspend operator fun invoke(userId: UUID, roleIds: List<Long>, scopeId: UUID): Unit = suspendTransactionWorker {
        roleRepository.setByUserAndScope(userId, roleIds, scopeId)
    }
}