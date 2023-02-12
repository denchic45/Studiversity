package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ScopeTypes : LongIdTable("scope_type", "scope_type_id") {
    val name = varcharMax("scope_type_name")
    val parent = reference("parent_type", ScopeTypes.id)
}

class ScopeTypeDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ScopeTypeDao>(ScopeTypes)

    var name by ScopeTypes.name
    var parentId by ScopeTypes.parent
}