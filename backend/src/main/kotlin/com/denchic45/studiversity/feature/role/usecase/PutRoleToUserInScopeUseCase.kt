package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class PutRoleToUserInScopeUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roleRepository: RoleRepository
) {
  suspend operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID): Unit = suspendTransactionWorker {
        if (!roleRepository.setByUserAndScope(userId, roleId, scopeId)) throw NotFoundException()
    }
}