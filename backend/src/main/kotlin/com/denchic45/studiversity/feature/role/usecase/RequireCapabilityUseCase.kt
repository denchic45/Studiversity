package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.studiversity.transaction.DatabaseTransactionWorker
import com.denchic45.stuiversity.api.role.model.Capability
import java.util.*

class RequireCapabilityUseCase(
    private val suspendTransactionWorker: DatabaseTransactionWorker,
    private val roleRepository: RoleRepository
) {

  operator fun invoke(userId: UUID, capability: Capability, scopeId: UUID) = suspendTransactionWorker {
        if (!roleRepository.hasCapability(userId, capability, scopeId)) {
            throw ForbiddenException()
        }
    }
}