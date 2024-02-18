package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object Capabilities : LongIdTable("capability", "capability_id") {
    val name = text("capability_name")
    val resource = text("capability_resource").uniqueIndex()
//    val scopeType = reference(
//        "scope_type_id", ScopeTypes,
//        onDelete = ReferenceOption.CASCADE,
//        onUpdate = ReferenceOption.CASCADE
//    )
}