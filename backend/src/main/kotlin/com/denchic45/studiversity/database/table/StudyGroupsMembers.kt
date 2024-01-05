package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Table

object StudyGroupsMembers : Table("study_group_member") {
    val studyGroupId = reference("study_group_id", StudyGroups.id)
    val memberId = reference("member_id", Users.id)
}