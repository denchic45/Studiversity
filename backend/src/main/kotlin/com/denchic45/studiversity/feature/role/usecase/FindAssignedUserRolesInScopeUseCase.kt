package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import java.util.*

class FindAssignedUserRolesInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId: UUID): UserRolesResponse = transactionWorker {
        roleRepository.findUserRolesByScopeId(userId, scopeId)
    }
}