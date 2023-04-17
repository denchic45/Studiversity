package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.RoleRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindAssignedUserRolesInScopeUseCase @javax.inject.Inject constructor(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(
        userId: UUID? = null,
        scopeId: UUID? = null
    ): Resource<UserRolesResponse> {
        return roleRepository.findRolesByUserAndScope(userId, scopeId)
    }
}