package com.denchic45.kts.domain.timetable.model

import com.denchic45.kts.domain.model.StudyGroupItem
import com.denchic45.kts.ui.model.UserItem
import java.util.*

data class PeriodItem(
    val id: UUID,
    val studyGroup: StudyGroupItem,
    val room: String?,
    val members: List<UserItem>,
    val details: PeriodDetails
)

sealed interface PeriodDetails

data class LessonDetails(
    val courseId: UUID,
    val subjectAvatarUrl: String,
    val subjectName: String
) : PeriodDetails

data class EventDetails(
    val name: String,
    val iconUrl: String,
    val color: String
) : PeriodDetails

object EmptyPeriod : PeriodDetails