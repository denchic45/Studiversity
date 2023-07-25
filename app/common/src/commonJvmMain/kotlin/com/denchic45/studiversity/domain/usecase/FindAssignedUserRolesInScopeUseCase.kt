package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindAssignedUserRolesInScopeUseCase(
    private val roleRepository: RoleRepository
) {
    operator fun invoke(
        userId: UUID? = null,
        scopeId: UUID? = null
    ): Flow<Resource<UserRolesResponse>> {
        return roleRepository.findRolesByUserAndScope(userId, scopeId)
    }
}