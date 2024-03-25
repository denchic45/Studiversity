package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Table

object UsersRolesScopes : Table("user_role_scope") {
    val userId = reference("user_id", Users)
    val roleId = reference("role_id", Roles)
    val scopeId = reference("scope_id", Scopes)

    init {
        uniqueIndex("user_role_scope_un", userId, roleId, scopeId)
    }
}

//class UserRoleScopeDao(id: EntityID<Long>) : LongEntity(id) {
//    companion object : LongEntityClass<UserRoleScopeDao>(UsersRolesScopes)
//
//    var userId by UsersRolesScopes.userId
//    var roleId by UsersRolesScopes.roleId
//    var scopeId by UsersRolesScopes.scopeId
//
//    var role by RoleDao referencedOn UsersRolesScopes.roleId
//    var user by UserDao referencedOn UsersRolesScopes.userId
//    var scope by ScopeDao referencedOn UsersRolesScopes.scopeId
//}