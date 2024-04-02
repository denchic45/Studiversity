package com.denchic45.studiversity.domain.model

import java.util.*

data class StudyGroupMembers(
    val studyGroupId: UUID,
    val curators: List<UserItem>,
    val headmanId: UUID?,
    val students: List<UserItem>,
    val otherMembers: List<UserItem>
) {
    fun isHeadman(memberId: UUID): Boolean {
        return headmanId == memberId
    }

    fun isEmpty(): Boolean {
        return students.isEmpty() && curators.isEmpty()
    }
}