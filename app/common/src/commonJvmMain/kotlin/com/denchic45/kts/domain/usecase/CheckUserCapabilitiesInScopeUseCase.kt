package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.RoleRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CheckUserCapabilitiesInScopeUseCase @javax.inject.Inject constructor(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(
        userId: UUID? = null,
        scopeId: UUID? = null,
        capabilities: List<Capability>,
    ): Resource<CheckCapabilitiesResponse> {
        return roleRepository.findUserCapabilitiesIdAndScopeId(userId, scopeId, capabilities)
    }
}