package com.studiversity.database.table

import com.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object Roles : LongIdTable("role", "role_id") {
    val name = varcharMax("role_name")
    val shortName = varcharMax("short_name")
    val parent = reference("parent", Roles).nullable()
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
    var shortName by Roles.shortName
    var parent by RoleDao optionalReferencedOn Roles.parent
}