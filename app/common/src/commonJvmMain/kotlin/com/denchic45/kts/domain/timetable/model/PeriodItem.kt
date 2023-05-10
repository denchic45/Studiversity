package com.denchic45.kts.domain.timetable.model

import com.denchic45.kts.domain.model.StudyGroupNameItem
import com.denchic45.kts.ui.model.UserItem
import java.time.Instant
import java.util.*

data class PeriodItem(
    val id: Long,
    val studyGroup: StudyGroupNameItem,
    val room: String?,
    val members: List<UserItem>,
    val details: PeriodDetails
) {
//    val timestamp = System.currentTimeMillis()
}

sealed interface PeriodDetails {
    data class Lesson(
        val courseId: UUID,
        val subjectIconUrl: String?,
        val subjectName: String?
    ) : PeriodDetails

    data class Event(
        val name: String,
        val iconUrl: String,
        val color: String
    ) : PeriodDetails
}