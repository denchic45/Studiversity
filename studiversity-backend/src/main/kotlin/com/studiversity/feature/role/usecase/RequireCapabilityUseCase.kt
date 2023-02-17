package com.studiversity.feature.role.usecase

import com.denchic45.stuiversity.api.role.model.Capability
import com.studiversity.feature.role.repository.RoleRepository
import com.studiversity.ktor.ForbiddenException
import com.studiversity.transaction.TransactionWorker
import java.util.*

class RequireCapabilityUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository) {

    operator fun invoke(userId: UUID, capability: Capability, scopeId: UUID) = transactionWorker {
        if (!roleRepository.hasCapability(userId, capability, scopeId)) {
            throw ForbiddenException()
        }
    }
}