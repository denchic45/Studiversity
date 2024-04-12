package com.denchic45.studiversity.feature.course.member

import com.denchic45.studiversity.api.common.SortOrder
import com.denchic45.studiversity.api.course.member.CourseMemberSorting
import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.database.distinctOn
import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.role.mapper.toRole
import com.denchic45.studiversity.feature.user.toUserResponse
import com.denchic45.studiversity.util.toSqlSortOrder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class CourseMemberRepository {

    fun findByCourseId(courseId: UUID, sorting: List<CourseMemberSorting>?): List<ScopeMember> {
        val roles = UsersRolesScopes.getUserRolesByScopeId(courseId)
            .groupBy { it[UsersRolesScopes.userId].value }
            .mapValues { (_, value) -> value.map { RoleDao.wrapRow(it).toRole() } }

        val membersQuery = Enrollments.innerJoin(UserEnrollments, { Enrollments.id }, { enrollmentId })
            .innerJoin(Users, { UserEnrollments.userId }, { Users.id })
            .select(Users.columns)
            .where { Enrollments.courseId eq courseId }
            .groupBy(Users.id)

        sorting?.forEach { sort ->
            when (sort) {
                is CourseMemberSorting.FullName -> membersQuery.orderBy(Users.surname, sort.order.toSqlSortOrder())

                is CourseMemberSorting.UpperParentRole -> {
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

        return UserDao.wrapRows(membersQuery).map {
            ScopeMember(
                user = it.toUserResponse(),
                roles = roles[it.id.value].orEmpty()
            )
        }
    }

    fun enroll(courseId: UUID, userId: UUID) {
        UserEnrollments.insert {
            it[UserEnrollments.userId] = userId
            it[enrollmentId] = Enrollments.selectAll()
                .where { Enrollments.courseId eq courseId and (Enrollments.type eq EnrollmentType.DEFAULT) }
                .single()[Enrollments.id]
        }
    }

    fun existMember(courseId: UUID, memberId: UUID): Boolean {
        return UserEnrollments.innerJoin(Enrollments, { enrollmentId }, { Enrollments.id })
            .exists { Enrollments.courseId eq courseId and (UserEnrollments.userId eq memberId) }
    }

    fun remove(courseId: UUID, userId: UUID) {
        val defaultEnrollment = EnrollmentDao.find(
            Enrollments.courseId eq courseId and (Enrollments.type eq EnrollmentType.DEFAULT)
        ).single()
        UserEnrollments.deleteWhere {
            UserEnrollments.userId eq userId and (enrollmentId eq defaultEnrollment.id)
        }
    }
}