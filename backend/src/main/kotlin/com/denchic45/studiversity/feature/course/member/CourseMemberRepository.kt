package com.denchic45.studiversity.feature.course.member

import com.denchic45.studiversity.database.distinctOn
import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.user.toUserResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.util.*

class CourseMemberRepository {

    fun findByCourseId(courseId: UUID): List<UserResponse> {
        val userIds = Enrollments.innerJoin(UserEnrollments, { Enrollments.id }, { enrollmentId })
            .slice(UserEnrollments.userId.distinctOn())
            .select { Enrollments.courseId eq courseId }
            .map { it[UserEnrollments.userId.distinctOn()].value }

        return UserDao.find(Users.id inList userIds).map(UserDao::toUserResponse)
    }


    fun enroll(courseId: UUID, userId: UUID) {
        UserEnrollments.insert {
            it[UserEnrollments.userId] = userId
            it[enrollmentId] = Enrollments.select {
                Enrollments.courseId eq courseId and (Enrollments.type eq EnrollmentType.DEFAULT)
            }.single()[Enrollments.id]
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
            UserEnrollments.userId eq userId and
                    (enrollmentId eq defaultEnrollment.id)
        }
    }
}