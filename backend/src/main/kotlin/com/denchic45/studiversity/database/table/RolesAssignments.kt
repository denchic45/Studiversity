package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object RolesAssignments : LongIdTable("role_assignment", "role_assignment_id") {
    val roleId = reference("role_id", Roles.id)
    val assignableRoleId = reference("assignable_role", Roles.id)

    init {
        uniqueIndex("role_assignment_un", roleId, assignableRoleId)
    }
}