package com.studiversity.feature.role.usecase

import com.studiversity.feature.role.RoleErrors
import com.studiversity.feature.role.repository.RoleRepository
import io.ktor.server.plugins.*
import java.util.*

class RequireAvailableRolesInScopeUseCase(private val roleRepository: RoleRepository) {
    operator fun invoke(rolesIds: List<Long>, scopeId: UUID) {
        if (!roleRepository.existRolesByScope(rolesIds, scopeId))
            throw BadRequestException(RoleErrors.NOT_AVAILABLE_ROLE_IN_SCOPE)
    }
}