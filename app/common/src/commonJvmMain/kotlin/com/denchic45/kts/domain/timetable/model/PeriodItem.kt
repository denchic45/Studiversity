package com.denchic45.kts.domain.timetable.model

import com.denchic45.kts.domain.model.StudyGroupNameItem
import com.denchic45.kts.ui.model.UserItem
import java.util.*

data class PeriodItem(
    val id: Long,
    val studyGroup: StudyGroupNameItem,
    val room: String?,
    val members: List<UserItem>,
    val order: Int,
    val details: PeriodDetails
)

sealed interface PeriodDetails {
    data class Lesson(
        val courseId: UUID,
        val subjectIconUrl: String,
        val subjectName: String
    ) : PeriodDetails

    data class Event(
        val name: String,
        val iconUrl: String,
        val color: String
    ) : PeriodDetails
}