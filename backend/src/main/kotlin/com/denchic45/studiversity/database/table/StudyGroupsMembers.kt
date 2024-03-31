package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object StudyGroupsMembers : Table("study_group_member") {
    val studyGroupId = reference("study_group_id", StudyGroups, onDelete = ReferenceOption.CASCADE)
    val memberId = reference("member_id", Users, onDelete = ReferenceOption.CASCADE)
}