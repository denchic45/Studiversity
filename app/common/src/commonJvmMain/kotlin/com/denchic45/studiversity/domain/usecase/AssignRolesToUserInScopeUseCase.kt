package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AssignRolesToUserInScopeUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(userId: UUID, scopeId: UUID, roleIds: List<Long>): EmptyResource {
        return roleRepository.addUserRoles(userId, roleIds, scopeId)
    }
}