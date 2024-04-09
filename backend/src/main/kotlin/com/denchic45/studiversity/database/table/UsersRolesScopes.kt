package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.innerJoin
import java.util.*

object UsersRolesScopes : Table("user_role_scope") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val roleId = reference("role_id", Roles, onDelete = ReferenceOption.CASCADE)
    val scopeId = reference("scope_id", Scopes, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex("user_role_scope_un", userId, roleId, scopeId)
    }
}

fun UsersRolesScopes.getUserRolesByScopeId(scopeId: UUID): Query {
    return UsersRolesScopes.innerJoin(Roles, { roleId }, { Roles.id })
        .select(roleId)
        .where { UsersRolesScopes.scopeId eq scopeId }
}