package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveUserRoleFromScopeUseCase(
    private val roleRepository: RoleRepository
) {

    suspend operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID) {
        roleRepository.removeUserRole(userId, roleId, scopeId)
    }
}