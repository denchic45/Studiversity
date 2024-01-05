package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

//object ExternalStudyGroupsMemberships : LongIdTable(
//    "external_study_group_membership", "external_study_group_membership_id"
//) {
//    val membershipId = reference("membership_id", Memberships.id)
//    val studyGroupId = reference("study_group_id", StudyGroups.id)
//
//    init {
//        uniqueIndex("external_study_group_membership_un", membershipId, studyGroupId)
//    }
//}