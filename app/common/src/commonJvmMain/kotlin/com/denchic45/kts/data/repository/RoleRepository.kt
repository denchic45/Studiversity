package com.denchic45.kts.data.repository

import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.role.CapabilityApi
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import java.util.*

class RoleRepository(
    private val roleApi: RoleApi,
    private val capabilityApi: CapabilityApi,
    override val networkService: NetworkService,
) : NetworkServiceOwner {

    suspend fun addUserRole(userId: UUID, roleId: Long, scopeId: UUID) = fetchResource {
        roleApi.assignRoleToUserInScope(userId, scopeId, roleId)
    }

    suspend fun removeUserRole(userId: UUID, roleId: Long, scopeId: UUID) = fetchResource {
        roleApi.deleteRoleFromUserInScope(userId, scopeId, roleId)
    }

    suspend fun findUserCapabilitiesIdAndScopeId(
        userId: UUID?  = null,
        scopeId: UUID,
        capabilities: List<Capability>,
    ): Resource<CheckCapabilitiesResponse> = fetchResource {
        capabilityApi.check(userId, scopeId, capabilities)
    }
}