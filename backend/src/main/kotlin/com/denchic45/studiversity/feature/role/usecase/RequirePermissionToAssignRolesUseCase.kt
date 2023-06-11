package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.RoleErrors
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.ktor.ForbiddenException
import java.util.*

class RequirePermissionToAssignRolesUseCase(private val roleRepository: RoleRepository) {

    operator fun invoke(userId: UUID, roles: List<Long>, scopeId: UUID) {
        if (!roleRepository.existPermissionRolesByUserAndScopeId(userId, roles, scopeId))
            throw ForbiddenException(RoleErrors.PERMISSION_DENIED_TO_ASSIGN_ROLE)
    }
}