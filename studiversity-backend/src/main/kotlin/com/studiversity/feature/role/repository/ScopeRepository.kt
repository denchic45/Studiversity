package com.studiversity.feature.role.repository

import com.studiversity.database.table.ScopeDao
import com.studiversity.database.table.ScopeTypeDao
import com.studiversity.database.table.Scopes
import com.studiversity.feature.role.ScopeType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class ScopeRepository {

    fun add(scopeId: UUID, scopeType: ScopeType, parentScopeId: UUID) {
        val parentScope = ScopeDao.findById(parentScopeId)!!
        val parentType = parentScope.type

        if (scopeType.parent?.id != parentType.id.value)
            throw IllegalArgumentException(
                "type of passed parentScopeId $parentScope is not parent type of passed scopeTypeId $scopeType"
            )

        ScopeDao.new(scopeId) {
            type = ScopeTypeDao.findById(scopeType.id)!!
            path = listOf(scopeId) + parentScope.path
        }
    }

    fun remove(id: UUID) {
        Scopes.deleteWhere { Scopes.id eq id }
    }
}