package com.denchic45.studiversity.database.table


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ScopeTypes : LongIdTable("scope_type", "scope_type_id") {
    val name = text("scope_type_name")
    val parent = reference("parent_type", ScopeTypes.id).nullable()
}

class ScopeTypeDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ScopeTypeDao>(ScopeTypes)

    var name by ScopeTypes.name

    //    var parentId by ScopeTypes.parent
    var parent by ScopeTypeDao optionalReferencedOn ScopeTypes.parent
}