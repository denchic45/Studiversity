package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.RoleRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import java.util.*

class CheckUserCapabilitiesInScopeUseCase(private val roleRepository: RoleRepository) {
    suspend operator fun invoke(
        userId: UUID,
        scopeId: UUID,
        capabilities: List<Capability>,
    ): Resource<CheckCapabilitiesResponse> {
        return roleRepository.findUserCapabilitiesIdAndScopeId(userId, scopeId, capabilities)
    }
}