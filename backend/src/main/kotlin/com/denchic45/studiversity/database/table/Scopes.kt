package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.database.distinctOn
import com.denchic45.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import java.util.*

object Scopes : UUIDTable("scope", "instance_id") {
    val type: Column<EntityID<Long>> = reference("type", ScopeTypes.id)
    val path = varcharMax("path")
}

class ScopeDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ScopeDao>(Scopes) {
        fun findByMemberId(memberId: UUID): SizedIterable<ScopeDao> = ScopeDao.forIds(findIdsByMemberId(memberId))

        fun findIdsByMemberId(memberId: UUID) = Memberships
            .innerJoin(UsersMemberships, { id }, { membershipId })
            .innerJoin(Scopes, { Memberships.scopeId }, { id })
            .slice(Scopes.id.distinctOn())
            .select(UsersMemberships.memberId eq memberId)
            .map { it[Scopes.id.distinctOn()].value }
    }

    var path: List<UUID> by Scopes.path.transform(
        toColumn = { it.reversed().joinToString("/") },
        toReal = { it.split("/").reversed().map(UUID::fromString) }
    )
    var type by ScopeTypeDao referencedOn Scopes.type

    var scopeTypeId by Scopes.type
}