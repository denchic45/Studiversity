package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Memberships : UUIDTable("membership", "membership_id") {
    val scopeId = uuid("scope_id").references(Scopes.id)
    val active = bool("active")
    val type = varcharMax("type")
}

class MembershipDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MembershipDao>(Memberships)

    var scopeId by Memberships.scopeId
    var active by Memberships.active
    var type by Memberships.type

    var scope by ScopeDao referencedOn Memberships.scopeId
}