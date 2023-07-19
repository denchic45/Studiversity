package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.RoleErrors
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.stuiversity.api.role.model.Role
import io.ktor.server.plugins.*

class FindRolesByNamesUseCase(private val roleRepository: RoleRepository) {

  suspend operator fun invoke(roleNames: List<String>): List<Role> {
        return roleRepository.findByNames(roleNames).let {
            if (it.contains(null))
                throw NotFoundException(RoleErrors.NOT_FOUND_ROLE)
            it.requireNoNulls()
        }
    }
}