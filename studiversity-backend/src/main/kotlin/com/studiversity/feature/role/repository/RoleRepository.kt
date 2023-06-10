package com.studiversity.feature.role.repository

import com.denchic45.stuiversity.api.role.model.*
import com.studiversity.config
import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.role.Permission
import com.studiversity.feature.role.combinedPermission
import com.studiversity.feature.role.mapper.toUserRolesResponse
import com.studiversity.feature.role.mapper.toUsersWithRoles
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class RoleRepository {

    fun hasRole(userId: UUID, role: Role, scopeId: UUID, checkParentScopes: Boolean = true): Boolean = transaction {
        val scope = (ScopeDao.findById(scopeId) ?: return@transaction false)

        if (!checkParentScopes)
            return@transaction isExistRoleOfUserByScope(userId, role.id, scopeId)

        val scopeIds = scope.path
        scopeIds.any { pathScopeId -> isExistRoleOfUserByScope(userId, role.id, pathScopeId) }
    }

    fun hasRoleIn(userId: UUID, role: Role, scopeIds: List<UUID>, checkParentScopes: Boolean = true): Boolean {
        return transaction {
            scopeIds.any { hasRole(userId, role, it, checkParentScopes) }
        }
    }

    private fun isExistRoleOfUserByScope(userId: UUID, roleId: Long, scopeId: UUID): Boolean {
        return UsersRolesScopes.exists {
            UsersRolesScopes.userId eq userId and
                    (UsersRolesScopes.roleId eq roleId) and
                    (UsersRolesScopes.scopeId eq scopeId)
        }
    }

    private fun getUserRoleIdsByScope(userId: UUID, scopeId: UUID): List<Long> {
        return UserRoleScopeDao.find(
            UsersRolesScopes.userId eq userId and (UsersRolesScopes.scopeId eq scopeId)
        ).map { it.roleId.value }
    }

    fun findCapabilitiesByUserIdAndScopeId(
        userId: UUID,
        scopeId: UUID,
        capabilities: List<String>
    ): CheckCapabilitiesResponse {
        return CheckCapabilitiesResponse(buildMap {
            capabilities.forEach { capability ->
                put(capability, hasCapability(userId, Capability(capability), scopeId))
            }
        })
    }

    fun hasCapability(userId: UUID, capability: Capability, scopeId: UUID): Boolean {
        val scope = ScopeDao.findById(scopeId) ?: throw NotFoundException("SCOPE_NOT_FOUND")
        val path = scope.path
        var has = false
        for (nextScopeId in path) {
            when (findCapabilityPermissionOfUserInScope(userId, nextScopeId, capability.toString())) {
                Permission.Undefined -> continue
                Permission.Allow -> has = true
                Permission.Prohibit -> {
                    has = false
                    break
                }
            }
        }
        return has

    }

    private fun findCapabilityPermissionOfUserInScope(
        userId: UUID,
        scopeId: UUID,
        capabilityResource: String
    ): Permission = UsersRolesScopes.slice(UsersRolesScopes.roleId).select(
        UsersRolesScopes.userId eq userId
                and (UsersRolesScopes.scopeId eq scopeId)
    ).map { usersRolesScopesRow ->
        val roleId = usersRolesScopesRow[UsersRolesScopes.roleId].value
        findPermissionByRole(
            roleIds = RoleDao.findParentRoleIdsByRoleId(roleId) + roleId,
            capabilityResource = capabilityResource
        )
    }.combinedPermission()

    private fun findPermissionByRole(
        roleIds: List<Long>,
        capabilityResource: String
    ) = RolesCapabilities.slice(RolesCapabilities.permission)
        .select(
            RolesCapabilities.roleId inList roleIds
                    and (RolesCapabilities.capabilityResource eq capabilityResource)
        ).map { rolesCapabilitiesRow -> rolesCapabilitiesRow[RolesCapabilities.permission] }
        .firstOrNull() ?: Permission.Undefined


    fun existRolesByScope(roles: List<Long>, scopeId: UUID): Boolean = transaction {
        roles.all { roleId ->
            val scopeType = ScopeDao.findById(scopeId)!!.scopeTypeId.value
            RolesScopes.exists { RolesScopes.roleId eq roleId and (RolesScopes.scopeId eq scopeType) }
        }
    }

    fun existPermissionRolesByUserAndScopeId(userId: UUID, assignRoles: List<Long>, scopeId: UUID) = transaction {
        assignRoles.all { assignRole -> existPermissionRoleByUserAndScopeId(userId, assignRole, scopeId) }
    }

    private fun existPermissionRoleByUserAndScopeId(userId: UUID, assignRoleId: Long, scopeId: UUID): Boolean {
        return ScopeDao.findById(scopeId)!!.path.any { segmentScopeId ->
            getUserRoleIdsByScope(userId, segmentScopeId).any { roleId ->
                existRoleAssignment(RoleDao.findParentRoleIdsByRoleId(roleId) + roleId, assignRoleId)
            }
        }
    }


    private fun existRoleAssignment(roleIds: List<Long>, assignRoleId: Long): Boolean {
        return RolesAssignments.exists {
            RolesAssignments.roleId inList roleIds and (RolesAssignments.assignableRoleId eq assignRoleId)
        }
    }

    fun findByNames(roleNames: List<String>) = transaction {
        roleNames.map { name ->
            RoleDao.find(Roles.shortName eq name)
                .singleOrNull()?.let { Role(it.id.value, it.shortName) }
        }
    }

    fun existUserByScope(userId: UUID, scopeId: UUID) = transaction {
        UsersRolesScopes.exists { UsersRolesScopes.userId eq userId and (UsersRolesScopes.scopeId eq scopeId) }
    }

    fun findUserRolesByScopeId(userId: UUID, scopeId: UUID): UserRolesResponse {
        val result = UserRoleScopeDao.find(
            UsersRolesScopes.userId eq userId
                    and (UsersRolesScopes.scopeId eq scopeId)
        )

        return if (scopeId == config.organization.id && result.empty()) {
            UserRolesResponse(userId, listOf(Role.User))
        } else {
            result.toUserRolesResponse(userId)
        }
    }

    fun findUsersByScopeId(scopeId: UUID): List<UserWithRolesResponse> = transaction {
        UserRoleScopeDao.find(UsersRolesScopes.scopeId eq scopeId).toUsersWithRoles()
    }

    fun addUserRolesToScope(userId: UUID, roles: List<Long>, scopeId: UUID) = transaction {
        roles.filterNot { roleId ->
            UsersRolesScopes.exists {
                UsersRolesScopes.userId eq userId and
                        (UsersRolesScopes.roleId eq roleId) and
                        (UsersRolesScopes.scopeId eq scopeId)
            }
        }.map { roleId ->
            UsersRolesScopes.insert {
                it[UsersRolesScopes.userId] = userId
                it[this.scopeId] = scopeId
                it[UsersRolesScopes.roleId] = roleId
            }.run { insertedCount > 0 }
        }.all { it }
    }

    fun removeUserRolesFromScope(userId: UUID, scopeId: UUID) = transaction {
        UsersRolesScopes.deleteWhere {
            UsersRolesScopes.scopeId eq scopeId and
                    (UsersRolesScopes.userId eq userId)
        }
    } > 0

    fun setByUserAndScope(userId: UUID, roleId: Long, scopeId: UUID) = UsersRolesScopes.insert {
        it[UsersRolesScopes.userId] = userId
        it[UsersRolesScopes.roleId] = roleId
        it[UsersRolesScopes.scopeId] = scopeId
    }.insertedCount > 0


    fun removeByUserAndScope(userId: UUID, roleId: Long, scopeId: UUID) {
        UsersRolesScopes.deleteWhere {
            UsersRolesScopes.userId eq userId and
                    (UsersRolesScopes.roleId eq roleId) and
                    (UsersRolesScopes.scopeId eq scopeId)
        }
    }
}