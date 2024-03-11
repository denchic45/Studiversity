package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FindAssignableRolesUseCase(
    private val roleRepository: RoleRepository
) {
    operator fun invoke(
        roleId: Long
    ): Flow<Resource<List<Role>>> {
        return roleRepository.findAssignableRoles(roleId)
    }
}