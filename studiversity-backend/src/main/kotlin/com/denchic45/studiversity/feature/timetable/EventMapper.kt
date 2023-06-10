package com.denchic45.studiversity.feature.timetable

import com.denchic45.stuiversity.api.timetable.model.*
import com.denchic45.studiversity.database.table.PeriodDao
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.feature.course.subject.toResponse
import com.denchic45.studiversity.feature.course.toResponse
import com.denchic45.studiversity.feature.room.toResponse

fun PeriodDao.toResponse() = when (type) {
    PeriodType.LESSON -> LessonResponse(
        id = id.value.toLong(),
        date = date,
        order = order,
        room = room?.toResponse(),
        studyGroup = studyGroup.let {
            StudyGroupName(
                id = it.id.value,
                name = it.name
            )
        },
        members = members.map { it.member.toResponse() },
        details = with(lesson) {
            LessonDetails(course = course.toResponse())
        }
    )

    PeriodType.EVENT -> EventResponse(
        id = id.value,
        date = date,
        order = order,
        room = room?.toResponse(),
        studyGroup = studyGroup.let {
            StudyGroupName(
                id = it.id.value,
                name = it.name
            )
        },
        members = members.map { it.member.toResponse() },
        details = with(event) {
            EventDetails(name = name, iconUrl = icon, color = color)
        }
    )
}

private fun UserDao.toResponse() = PeriodMember(
    id = id.value,
    firstName = firstName,
    surname = surname,
    avatarUrl = avatarUrl
)