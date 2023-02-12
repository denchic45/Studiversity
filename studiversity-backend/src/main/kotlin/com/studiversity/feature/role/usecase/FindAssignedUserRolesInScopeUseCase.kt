package com.studiversity.feature.role.usecase

import com.stuiversity.api.role.model.UserRolesResponse
import com.studiversity.feature.role.repository.RoleRepository
import com.studiversity.transaction.TransactionWorker
import java.util.*

class FindAssignedUserRolesInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId: UUID): UserRolesResponse = transactionWorker {
        roleRepository.findUserRolesByScopeId(userId, scopeId)
    }
}