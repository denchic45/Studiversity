package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object Scopes : UUIDTable("scope", "instance_id") {
    val type: Column<EntityID<Long>> = reference("type", ScopeTypes.id)
    val path = varcharMax("path")
}

class ScopeDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ScopeDao>(Scopes)

    var path: List<UUID> by Scopes.path.transform(
        toColumn = { it.reversed().joinToString("/") },
        toReal = { it.split("/").reversed().map(UUID::fromString) }
    )
    var type by ScopeTypeDao referencedOn Scopes.type

    var scopeTypeId by Scopes.type
}