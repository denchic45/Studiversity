package com.studiversity.feature.role.usecase

import com.studiversity.feature.role.RoleErrors
import com.studiversity.feature.role.repository.RoleRepository
import com.studiversity.ktor.ForbiddenException
import java.util.*

class RequirePermissionToAssignRolesUseCase(private val roleRepository: RoleRepository) {

    operator fun invoke(userId: UUID, roles: List<Long>, scopeId: UUID) {
        if (!roleRepository.existPermissionRolesByUserAndScopeId(userId, roles, scopeId))
            throw ForbiddenException(RoleErrors.PERMISSION_DENIED_TO_ASSIGN_ROLE)
    }
}