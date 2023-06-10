package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object RolesScopes : IntIdTable("role_scope", "role_scope_id") {
    val roleId = long("role_id")
    val scopeId = long("scope_id")

    init {
        uniqueIndex("role_scope_un", roleId, scopeId)
    }
}