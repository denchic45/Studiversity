package com.studiversity.feature.timetable

import com.studiversity.database.table.PeriodDao
import com.stuiversity.api.timetable.model.*

fun PeriodDao.toResponse() = when (type) {
    PeriodType.LESSON -> LessonResponse(
        id = id.value,
        date = date,
        order = order,
        roomId = roomId,
        studyGroupId = studyGroupId,
        memberIds = members.map { it.member.id.value },
        details = with(lesson) {
            LessonDetails(courseId = courseId)
        }
    )

    PeriodType.EVENT -> EventResponse(
        id = id.value,
        date = date,
        order = order,
        roomId = roomId,
        studyGroupId = studyGroupId,
        memberIds = members.map { it.member.id.value },
        details = with(event) {
            EventDetails(name = name, icon = icon, color = color)
        }
    )
}