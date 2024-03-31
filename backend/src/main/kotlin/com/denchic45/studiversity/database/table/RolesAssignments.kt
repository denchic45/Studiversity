package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Table

object RolesAssignments : Table("role_assignment") {
    val roleId = reference("role_id", Roles)
    val assignableRoleId = reference("assignable_role", Roles)

    init {
        uniqueIndex("role_assignment_un", roleId, assignableRoleId)
    }
}