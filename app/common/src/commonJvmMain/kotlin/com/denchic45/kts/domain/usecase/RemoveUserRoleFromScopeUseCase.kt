package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.RoleRepository
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveUserRoleFromScopeUseCase @Inject constructor(
    private val roleRepository: RoleRepository
) {

    suspend operator fun invoke(userId: UUID, roleId: Long, scopeId: UUID) {
        roleRepository.removeUserRole(userId, roleId, scopeId)
    }
}