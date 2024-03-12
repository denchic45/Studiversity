package com.denchic45.studiversity.feature.auth.usecase

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.user.UserRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import java.util.*

class UpdateUserUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(userId: UUID, request: UpdateUserRequest) = suspendTransactionWorker.invoke {
        userRepository.updateUser(userId, request)
        request.roleIds.ifPresent { roleIds ->
            roleRepository.updateUserRolesInScope(userId, roleIds, config.organizationId)
        }
    }
}