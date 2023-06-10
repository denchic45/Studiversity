package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CheckUserCapabilitiesInScopeUseCase @javax.inject.Inject constructor(
    private val roleRepository: RoleRepository
) {
    operator fun invoke(
        userId: UUID? = null,
        scopeId: UUID? = null,
        capabilities: List<Capability>,
    ): Flow<Resource<CheckCapabilitiesResponse>> {
        return roleRepository.findUserCapabilitiesIdAndScopeId(userId, scopeId, capabilities)
    }
}