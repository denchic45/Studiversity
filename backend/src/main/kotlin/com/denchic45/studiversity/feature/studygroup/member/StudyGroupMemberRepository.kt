package com.denchic45.studiversity.feature.studygroup.member

import com.denchic45.studiversity.api.common.SortOrder
import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.api.studygroup.member.StudyGroupMemberSorting
import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.role.mapper.toRole
import com.denchic45.studiversity.feature.user.toUserResponse
import com.denchic45.studiversity.util.toSqlSortOrder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class StudyGroupMemberRepository {

    fun findByStudyGroupId(studyGroupId: UUID, sorting: List<StudyGroupMemberSorting>?): List<ScopeMember> {
        val roles = UsersRolesScopes.getUserRolesByScopeId(studyGroupId)
            .groupBy { it[UsersRolesScopes.userId].value }
            .mapValues { (_, value) -> value.map { RoleDao.wrapRow(it).toRole() } }

        val membersQuery = StudyGroupsMembers.innerJoin(Users, { memberId }, { Users.id })
            .select(Users.columns  + StudyGroupsMembers.memberId)
            .where(StudyGroupsMembers.studyGroupId eq studyGroupId)

        sorting?.forEach { sort ->
            when (sort) {
                is StudyGroupMemberSorting.FullName -> membersQuery.orderBy(Users.surname, sort.order.toSqlSortOrder())

                is StudyGroupMemberSorting.UpperParentRole -> {
                    val upperParentRolesTable = Roles.alias("upper_parent_roles")
                    membersQuery.adjustColumnSet {
                        leftJoin(upperParentRolesTable, { Roles.upperParent }, { upperParentRolesTable[Roles.id] })
                    }.orderBy(
                        upperParentRolesTable[Roles.order] to when (sort.order) {
                            SortOrder.ASC -> org.jetbrains.exposed.sql.SortOrder.ASC_NULLS_LAST
                            SortOrder.DESC -> org.jetbrains.exposed.sql.SortOrder.DESC_NULLS_LAST
                        }
                    )
                }
            }
        }

        return membersQuery.groupBy { it[Users.id].value }.map { (userId, rows) ->
            ScopeMember(
                user = UserDao.wrapRow(rows.first()).toUserResponse(),
                roles = roles[userId].orEmpty()
            )
        }
    }

    fun add(studyGroupId: UUID, memberId: UUID) {
        StudyGroupsMembers.insert {
            it[StudyGroupsMembers.studyGroupId] = studyGroupId
            it[StudyGroupsMembers.memberId] = memberId
        }
    }

    fun existMember(studyGroupId: UUID, memberId: UUID): Boolean {
        return StudyGroupsMembers.exists {
            StudyGroupsMembers.studyGroupId eq studyGroupId and (UserEnrollments.userId eq memberId)
        }
    }

    fun remove(studyGroupId: UUID, memberId: UUID) {
        StudyGroupsMembers.deleteWhere {
            StudyGroupsMembers.studyGroupId eq studyGroupId and (StudyGroupsMembers.memberId eq memberId)
        }
    }
}