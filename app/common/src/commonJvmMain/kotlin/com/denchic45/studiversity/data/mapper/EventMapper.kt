package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.EventEntity
import com.denchic45.studiversity.EventWithSubjectAndGroupAndTeachers
import com.denchic45.studiversity.TeacherEventEntity
import com.denchic45.studiversity.data.db.remote.model.EventMap
import com.denchic45.studiversity.data.domain.model.EventType
import com.denchic45.studiversity.data.domain.model.UserRole
import com.denchic45.studiversity.domain.model.*
import com.denchic45.studiversity.util.FireMap
import com.denchic45.stuiversity.util.toDate
import java.time.LocalDate
import java.util.*

fun EventMap.mapToEntity(dayId: String) = EventEntity(
    event_id = id,
    day_id = dayId,
    position = position,
    room = room,
    type = eventDetailsDoc.eventType,
    event_name = eventDetailsDoc.name,
    event_icon_name = eventDetailsDoc.iconName,
    color = eventDetailsDoc.color,
    subject_id = eventDetailsDoc.subjectId,
    teacher_ids = eventDetailsDoc.teacherIds,
    group_id = groupId
)

fun List<EventMap>.docsToEntities(dayId: String): List<EventEntity> {
    return map { eventDoc -> eventDoc.mapToEntity(dayId) }
}

fun List<EventEntity>.toTeacherEventEntities(): List<TeacherEventEntity> {
    return filterNot { it.teacher_ids.isNullOrEmpty() }
        .flatMap { eventEntity ->
            eventEntity.teacher_ids!!.map { id: String ->
                TeacherEventEntity(
                    eventEntity.event_id,
                    id
                )
            }
        }
}

//fun Event.domainToMap() = mutableMapOf<String, Any?>(
//    "id" to id,
//    "date" to date.toDate(),
//    "position" to order,
//    "room" to room,
//    "groupId" to groupHeader.id,
//    "eventDetailsDoc" to details.detailsToDetailsMap()
//)

//fun EventDetails.detailsToDetailsMap(): FireMap {
//    return HashMap<String, Any>().apply {
//        put("eventType", eventType.name)
//        when (this@detailsToDetailsMap) {
//            is Lesson -> {
//                put("subjectId", subject.id)
//                put("teacherIds", teachers.map(User::id))
//            }
//            is SimpleEventDetails -> {
//                put("name", name)
//                put("iconUrl", iconUrl)
//                put("color", color)
//            }
//            is EmptyEventDetails -> {}
//        }
//    }
//}

//fun List<EventWithSubjectAndGroupAndTeachers>.entitiesToEventsOfDay(date: LocalDate): EventsOfDay {
//    return EventsOfDay(date, entitiesToDomains(), id = first().day_id)
//}