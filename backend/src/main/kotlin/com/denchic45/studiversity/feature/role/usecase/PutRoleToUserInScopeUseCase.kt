package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class PutRoleToUserInScopeUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID): Unit = transactionWorker {
        if (!roleRepository.setByUserAndScope(userId, roleId, scopeId)) throw NotFoundException()
    }
}