package com.denchic45.studiversity.feature.studygroup.member

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.StudyGroupsMembers
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.database.table.UserEnrollments
import com.denchic45.studiversity.database.table.Users
import com.denchic45.studiversity.feature.user.toUserResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class StudyGroupMemberRepository {

    fun findByStudyGroupId(studyGroupId: UUID): List<UserResponse> {
        return UserDao.wrapRows(StudyGroupsMembers.innerJoin(Users, { memberId }, { Users.id })
            .select { StudyGroupsMembers.studyGroupId eq studyGroupId })
            .map(UserDao::toUserResponse)
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