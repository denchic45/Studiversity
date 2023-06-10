package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class CheckUserCapabilitiesInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId: UUID, capabilities: List<String>) = transactionWorker {
        roleRepository.findCapabilitiesByUserIdAndScopeId(userId, scopeId, capabilities)
    }
}