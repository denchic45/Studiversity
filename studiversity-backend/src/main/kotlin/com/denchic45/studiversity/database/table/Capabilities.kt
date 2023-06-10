package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.id.LongIdTable

object Capabilities : LongIdTable("capability", "capability_id") {
    val name = varcharMax("capability_name")
    val resource = varcharMax("capability_resource")
    val scopeType = reference("scope_type_id", ScopeTypes.id)
}