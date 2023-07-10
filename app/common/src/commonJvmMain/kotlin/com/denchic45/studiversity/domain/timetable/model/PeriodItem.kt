package com.denchic45.studiversity.domain.timetable.model

import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.stuiversity.api.room.model.RoomResponse
import java.util.UUID
import kotlin.random.Random

sealed interface PeriodSlot {
    val id: Long
}

data class PeriodItem(
    override val id: Long,
    val studyGroup: StudyGroupItem,
    val room: RoomResponse?,
    val members: List<UserItem>,
    val details: PeriodDetails,
) : PeriodSlot {
//    val timestamp = System.currentTimeMillis()
}

data class Window(override val id: Long = Random.nextLong(0, 1000)) : PeriodSlot


sealed interface PeriodDetails {
    data class Lesson(
        val course: CourseItem
    ) : PeriodDetails

    data class Event(
        val name: String,
        val iconUrl: String,
        val color: String,
    ) : PeriodDetails
}

data class RoomItem(
    val id: UUID,
    val name: String
)

