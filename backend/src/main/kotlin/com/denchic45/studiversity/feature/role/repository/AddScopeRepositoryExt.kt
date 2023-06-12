package com.denchic45.studiversity.feature.role.repository

import com.denchic45.studiversity.database.table.ScopeDao
import com.denchic45.studiversity.database.table.ScopeTypeDao
import com.denchic45.studiversity.feature.role.ScopeType
import java.util.*

interface AddScopeRepoExt {
    fun addScope(scopeId: UUID, scopeType: ScopeType, parentScopeId: UUID) {
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
}