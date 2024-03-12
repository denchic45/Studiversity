package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.role.CapabilityApi
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.util.uuidOrMe
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RoleRepository(
    private val appPreferences: AppPreferences,
    private val roleApi: RoleApi,
    private val capabilityApi: CapabilityApi,
    override val networkService: NetworkService,
) : NetworkServiceOwner {

    suspend fun addUserRole(userId: UUID, roleId: Long, scopeId: UUID) = fetchResource {
        roleApi.assignRoleToUserInScope(userId, scopeId, roleId)
    }

    suspend fun addUserRoles(userId: UUID, roleIds: List<Long>, scopeId: UUID) = fetchResource {
        roleApi.assignRolesToUserInScope(userId, scopeId, roleIds)
    }

    suspend fun removeUserRole(userId: UUID, roleId: Long, scopeId: UUID) = fetchResource {
        roleApi.deleteRoleFromUserInScope(userId, scopeId, roleId)
    }

    fun findUserCapabilitiesIdAndScopeId(
        userId: UUID? = null,
        scopeId: UUID? = null,
        capabilities: List<Capability>,
    ): Flow<Resource<CheckCapabilitiesResponse>> = fetchResourceFlow {
        capabilityApi.check(userId, scopeId ?: appPreferences.organizationId.toUUID(), capabilities)
    }

    fun findRolesByUserAndScope(
        userId: UUID?,
        scopeId: UUID?,
    ): Flow<Resource<UserRolesResponse>> = fetchResourceFlow {
        roleApi.getUserRolesInScope(
            uuidOrMe(userId),
            scopeId ?: appPreferences.organizationId.toUUID()
        )
    }

    fun findAssignableRoles(roleId: Long): Flow<Resource<List<Role>>> = fetchResourceFlow {
        roleApi.getAssignableRoles(roleId)
    }

    fun findAssignableRolesByUserAndScope(userId: UUID?, scopeId: UUID?) = fetchResourceFlow {
        roleApi.getAssignableRolesByUserAndScope(uuidOrMe(userId), scopeId ?: appPreferences.organizationId.toUUID())
    }
}