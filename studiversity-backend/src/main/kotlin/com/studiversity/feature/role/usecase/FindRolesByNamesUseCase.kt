package com.studiversity.feature.role.usecase

import com.denchic45.stuiversity.api.role.model.Role
import com.studiversity.feature.role.RoleErrors
import com.studiversity.feature.role.repository.RoleRepository
import io.ktor.server.plugins.*

class FindRolesByNamesUseCase(private val roleRepository: RoleRepository) {

    operator fun invoke(roleNames: List<String>): List<Role> {
        return roleRepository.findByNames(roleNames).let {
            if (it.contains(null))
                throw NotFoundException(RoleErrors.NOT_FOUND_ROLE)
            it.requireNoNulls()
        }
    }
}