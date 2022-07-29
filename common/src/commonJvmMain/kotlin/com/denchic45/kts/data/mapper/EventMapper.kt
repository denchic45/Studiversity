package com.denchic45.kts.data.mapper

import com.denchic45.kts.EventEntity
import com.denchic45.kts.EventWithSubjectAndGroupAndTeachers
import com.denchic45.kts.TeacherEventEntity
import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.remote.model.EventMap
import com.denchic45.kts.domain.model.*
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.toDate
import java.time.LocalDate
import java.util.*

fun EventMap.docToEntity(dayId: String) = EventEntity(
    event_id = id,
    day_id = dayId,
    position = position,
    room = room,
    type = eventDetailsDoc.type,
    event_name = eventDetailsDoc.name,
    event_icon_url = eventDetailsDoc.iconUrl,
    color = eventDetailsDoc.color,
    subject_id = eventDetailsDoc.subjectId,
    teacher_ids = eventDetailsDoc.teacherIds,
    group_id = groupId
)

fun List<EventMap>.docsToEntities(dayId: String): List<EventEntity> {
    return map { eventDoc -> eventDoc.docToEntity(dayId) }
}

fun List<EventEntity>.toTeacherEventEntities(): List<TeacherEventEntity> {
    return filter { it.teacher_ids.isNotEmpty() }
        .flatMap { eventEntity ->
            eventEntity.teacher_ids.map { id: String ->
                TeacherEventEntity(
                    eventEntity.event_id,
                    id
                )
            }
        }
}

fun List<EventWithSubjectAndGroupAndTeachers>.entityToUserDomain(): Event = first().run {
    Event(
        id = event_id,
        groupHeader = GroupHeader(
            id = group_id,
            name = group_name,
            specialtyId = specialty_id
        ),
        timestamp = Date(timestamp),
        details = when (EventType.valueOf(type)) {
            EventType.LESSON -> Lesson(
                Subject(
                    id = subject_id,
                    name = subject_name,
                    iconUrl = icon_url,
                    colorName = color_name
                ),
                map {
                    User(
                        id = user_id,
                        firstName = first_name,
                        surname = surname,
                        patronymic = patronymic,
                        groupId = user_group_id,
                        photoUrl = photo_url,
                        role = UserRole.valueOf(role),
                        email = email,
                        timestamp = Date(timestamp),
                        gender = gender,
                        generatedAvatar = generated_avatar,
                        admin = admin
                    )
                }
            )
            EventType.SIMPLE -> SimpleEventDetails(
                id = event_id,
                name = event_name!!,
                iconUrl = event_icon_url!!,
                color = color!!
            )
            EventType.EMPTY -> EmptyEventDetails()
        },
        room = room)
}

fun List<EventWithSubjectAndGroupAndTeachers>.entitiesToDomains(): List<Event> =
    groupBy { it.event_id }.map { it.value.entityToUserDomain() }

fun Event.domainToMap() = mutableMapOf(
    "id" to id,
    "date" to date.toDate(),
    "position" to order,
    "room" to room,
    "groupId" to groupHeader.id,
    "eventDetailsDoc" to details.detailsToDetailsDoc()
)

fun List<Event>.domainsToMaps() = map { it.domainToMap() }

fun EventDetails.detailsToDetailsDoc(): FireMap {
    return HashMap<String, Any>().apply {
        put("eventType", eventType)
        when (this@detailsToDetailsDoc) {
            is Lesson -> {
                put("subjectId", subject.id)
                put("teacherIds", teachers.map(User::id))
            }
            is SimpleEventDetails -> {
                put("name", name)
                put("iconUrl", iconUrl)
                put("color", color)
            }
        }
    }
}

fun List<EventWithSubjectAndGroupAndTeachers>.entitiesToEventsOfDay(date: LocalDate): EventsOfDay {
    return EventsOfDay(date, entitiesToDomains())
}