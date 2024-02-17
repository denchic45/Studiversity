package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object Roles : LongIdTable("role", "role_id") {
    val name = text("role_name")
    val shortname = text("shortname")
    val scopeTypeId = reference("scope_type_id", ScopeTypes)
    val parent = reference("parent", Roles).nullable()
    val order = integer("role_order").nullable()
}

class RoleDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RoleDao>(Roles) {

        fun findChildRoleIdsByRoleId(parentRoleId: Long): List<Long> {
            val childRoleIds = find(Roles.parent eq parentRoleId).map { it.id.value }
            if (childRoleIds.isEmpty()) return emptyList()
            return childRoleIds + buildList(childRoleIds.size) {
                childRoleIds.forEach { roleId -> addAll(findChildRoleIdsByRoleId(roleId)) }
            }
        }

        fun findParentRoleIdsByRoleId(childRoleId: Long): List<Long> {
            val parentRole = (findById(childRoleId)!!).parent ?: return emptyList()
            val parentRoles = mutableListOf(parentRole)
            while (true) {
                parentRoles.last().parent?.let {
                    parentRoles.add(it)
                } ?: break
            }
            return parentRoles.map { it.id.value }
        }
    }

    var name by Roles.name
    var shortname by Roles.shortname
    var scopeType by ScopeTypeDao referencedOn Roles.scopeTypeId
    var parent by RoleDao optionalReferencedOn Roles.parent
    var order by Roles.order
}