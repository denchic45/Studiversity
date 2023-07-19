package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import java.util.*

class FindAssignedUserRolesInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roleRepository: RoleRepository
) {
  suspend operator fun invoke(userId: UUID, scopeId: UUID): UserRolesResponse = suspendTransactionWorker {
        roleRepository.findUserRolesByScopeId(userId, scopeId)
    }
}