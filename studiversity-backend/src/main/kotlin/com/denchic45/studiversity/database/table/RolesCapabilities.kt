package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.feature.role.Permission
import org.jetbrains.exposed.sql.Table

object RolesCapabilities : Table("role_capability") {
    val roleId = long("role_id")
    val capabilityResource = reference("capability_resource", Capabilities.resource)
    val permission = enumeration<Permission>("permission")

    init {
        uniqueIndex("role_capability_un", roleId, capabilityResource)
    }
}