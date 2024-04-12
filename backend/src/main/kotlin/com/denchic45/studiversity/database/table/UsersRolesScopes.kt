package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.*
import java.util.*

object UsersRolesScopes : Table("user_role_scope") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val roleId = reference("role_id", Roles, onDelete = ReferenceOption.CASCADE)
    val scopeId = reference("scope_id", Scopes, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex("user_role_scope_un", userId, roleId, scopeId)
    }

    fun getUserRolesByScopeId(scopeId: UUID): Query {
        return innerJoin(Roles, { roleId }, { Roles.id })
            .selectAll()
            .where { UsersRolesScopes.scopeId eq scopeId }
    }
}