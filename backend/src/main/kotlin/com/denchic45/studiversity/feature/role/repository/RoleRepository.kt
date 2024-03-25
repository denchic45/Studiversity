package com.denchic45.studiversity.feature.role.repository

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.role.Permission
import com.denchic45.studiversity.feature.role.combinedPermission
import com.denchic45.studiversity.feature.role.mapper.toRole
import com.denchic45.stuiversity.api.role.model.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
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

    fun isExistRoleOfUserByScope(userId: UUID, roleId: Long, scopeId: UUID): Boolean {
        return UsersRolesScopes.exists {
            UsersRolesScopes.userId eq userId and
                    (UsersRolesScopes.roleId eq roleId) and
                    (UsersRolesScopes.scopeId eq scopeId)
        }
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
                Permission.UNDEFINED -> continue
                Permission.ALLOW -> has = true
                Permission.PROHIBIT -> {
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
    ): Permission = UsersRolesScopes.select(UsersRolesScopes.roleId).where(
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
    ) = RolesCapabilities.select(RolesCapabilities.permission)
        .where(
            RolesCapabilities.roleId inList roleIds
                    and (RolesCapabilities.capabilityResource eq capabilityResource)
        ).map { rolesCapabilitiesRow -> rolesCapabilitiesRow[RolesCapabilities.permission] }
        .firstOrNull() ?: Permission.UNDEFINED


    fun existRolesByScope(roles: List<Long>, scopeId: UUID): Boolean = transaction {
        roles.all { roleId ->
            RoleDao[roleId].scopeType.id == ScopeDao[scopeId].id
        }
    }

    fun existPermissionRolesByUserAndScopeId(userId: UUID, assignRoles: List<Long>, scopeId: UUID) = transaction {
        assignRoles.all { assignRole -> existPermissionRoleByUserAndScopeId(userId, assignRole, scopeId) }
    }

    private fun existPermissionRoleByUserAndScopeId(userId: UUID, assignRoleId: Long, scopeId: UUID): Boolean {
        return ScopeDao.findById(scopeId)!!.path.any { segmentScopeId ->
            getUserRoleIdsInScope(userId, segmentScopeId).any { roleId ->
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
            RoleDao.find(Roles.shortname eq name)
                .singleOrNull()?.let { Role(it.id.value, it.shortname) }
        }
    }

    fun findAssignableRoles(roleId: Long) = RolesAssignments.innerJoin(Roles, { RolesAssignments.roleId }, { Roles.id })
        .select(Roles.columns).where(RolesAssignments.assignableRoleId eq roleId)
        .let(RoleDao::wrapRows)
        .map(RoleDao::toRole)

    fun findAssignableRolesByUserAndScope(userId: UUID, scopeId: UUID): List<Role> {
        val scopeType = ScopeDao[scopeId].type
        val userRolesInScope = getUserRoleIdsInScope(userId, scopeId)

        return RolesAssignments
            .innerJoin(Roles, { assignableRoleId }, { Roles.id })
            .select(Roles.columns)
            .where(RolesAssignments.roleId inList userRolesInScope and (Roles.scopeTypeId eq scopeType.id))
//            .where(UsersRolesScopes.scopeId eq scopeId and (UsersRolesScopes.userId eq userId))
            .let(RoleDao::wrapRows)
            .map(RoleDao::toRole)
    }

    fun existUserByScope(userId: UUID, scopeId: UUID) = transaction {
        UsersRolesScopes.exists { UsersRolesScopes.userId eq userId and (UsersRolesScopes.scopeId eq scopeId) }
    }

    fun findUserRolesByScopeId(userId: UUID, scopeId: UUID): UserRolesResponse {
        val result = getUserRoleIdsInScope(userId, scopeId)
        return when {
            result.isNotEmpty() -> UserRolesResponse(
                userId = userId,
                roles = result.map { RoleDao[it].toRole() }
            )

            UserDao.isExistById(userId) && scopeId == config.organizationId -> {
                UserRolesResponse(userId, listOf(Role.User))
            }

            else -> throw NotFoundException("USER_NOT_FOUND")
        }
    }

    private fun getUserRoleIdsInScope(userId: UUID, scopeId: UUID): List<Long> {
        return UsersRolesScopes.select(UsersRolesScopes.roleId)
            .where(UsersRolesScopes.userId eq userId and (UsersRolesScopes.scopeId eq scopeId))
            .map { it[UsersRolesScopes.roleId].value }
    }

    fun findUsersByScopeId(scopeId: UUID): List<UserWithRolesResponse> = transaction {
        UsersRolesScopes.select(UsersRolesScopes.userId)
            .where(UsersRolesScopes.scopeId eq scopeId)
            .groupBy { it[UsersRolesScopes.userId].value }
            .map { (userId, rows) ->
                val userDao = UserDao[userId]
                UserWithRolesResponse(id = userId,
                    firstName = userDao.firstName,
                    surname = userDao.surname,
                    patronymic = userDao.patronymic,
                    roles = rows.map { RoleDao[it[UsersRolesScopes.roleId]].toRole() }
                )
            }
    }

    fun findUsersIdsByScopeIdAndRoleId(scopeId: UUID, roleId: Long): List<UUID> {
        return UsersRolesScopes.select(UsersRolesScopes.userId)
            .where(UsersRolesScopes.scopeId eq scopeId and (UsersRolesScopes.roleId eq roleId))
            .map { it[UsersRolesScopes.userId].value }
    }

    fun addUserRolesInScope(userId: UUID, roles: List<Long>, scopeId: UUID) {
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

    fun updateUserRolesInScope(userId: UUID, roleIds: List<Long>, scopeId: UUID) {
        val currentUserRoles = findUserRolesByScopeId(userId, scopeId)
        val currentRoleIds = currentUserRoles.roles.map(Role::id)

        val addedRoles = roleIds - currentRoleIds.toSet()
        val removedRoles = currentRoleIds - roleIds.toSet()

        addUserRolesInScope(userId, addedRoles, scopeId)
        removeUserRolesFromScope(userId, removedRoles, scopeId)
    }

    fun setByUserAndScope(userId: UUID, roleId: Long, scopeId: UUID) = UsersRolesScopes.insert {
        it[UsersRolesScopes.userId] = userId
        it[UsersRolesScopes.roleId] = roleId
        it[UsersRolesScopes.scopeId] = scopeId
    }.insertedCount > 0

    fun setByUserAndScope(userId: UUID, roleIds: List<Long>, scopeId: UUID) {
        removeUserRolesFromScope(userId, scopeId)
        roleIds.forEach { roleId ->
            setByUserAndScope(userId, roleId, scopeId)
        }
    }

    fun removeUserRolesFromScope(userId: UUID, scopeId: UUID) = UsersRolesScopes.deleteWhere {
        UsersRolesScopes.scopeId eq scopeId and
                (UsersRolesScopes.userId eq userId)
    } > 0

    private fun removeUserRolesFromScope(userId: UUID, roleIds: List<Long>, scopeId: UUID) =
        UsersRolesScopes.deleteWhere {
            UsersRolesScopes.scopeId eq scopeId and (UsersRolesScopes.userId eq userId) and (roleId inList roleIds)
        }


    fun removeRoleByUserAndScope(userId: UUID, roleId: Long, scopeId: UUID) {
        UsersRolesScopes.deleteWhere {
            UsersRolesScopes.userId eq userId and
                    (UsersRolesScopes.roleId eq roleId) and
                    (UsersRolesScopes.scopeId eq scopeId)
        }
    }
}