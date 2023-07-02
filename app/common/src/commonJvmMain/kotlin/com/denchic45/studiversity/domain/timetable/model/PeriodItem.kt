package com.denchic45.studiversity.domain.timetable.model

import com.denchic45.studiversity.domain.model.StudyGroupNameItem
import com.denchic45.studiversity.ui.model.UserItem
import java.util.UUID
import kotlin.random.Random

sealed interface PeriodSlot {
    val id: Long
}

data class PeriodItem(
    override val id: Long,
    val studyGroup: StudyGroupNameItem,
    val room: String?,
    val members: List<UserItem>,
    val details: PeriodDetails,
) : PeriodSlot {
//    val timestamp = System.currentTimeMillis()
}

data class Window(override val id: Long = Random.nextLong(0, 1000)) : PeriodSlot


sealed interface PeriodDetails {
    data class Lesson(
        val courseId: UUID,
        val subjectIconUrl: String?,
        val subjectName: String?,
    ) : PeriodDetails

    data class Event(
        val name: String,
        val iconUrl: String,
        val color: String,
    ) : PeriodDetails
}