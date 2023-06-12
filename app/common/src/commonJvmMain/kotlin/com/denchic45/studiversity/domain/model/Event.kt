package com.denchic45.studiversity.domain.model

import com.denchic45.studiversity.data.domain.model.EventType
import com.denchic45.studiversity.data.domain.model.DomainModel
import com.denchic45.studiversity.util.UUIDS
import java.time.LocalDate
import java.util.*

//data class Event(
//    override val id: UUID,
//    val groupHeader: GroupHeader,
//    val timestamp: Date? = null,
//    val room: String = "",
//    val details: EventDetails = EmptyEventDetails()
//) : DomainModel {
//
//    var eventsOfDay: EventsOfDay? = null
//
//    val isAttached: Boolean
//        get() = eventsOfDay != null
//
//    val date: LocalDate
//        get() = requireEventsOfDay().date
//
//    val isEmpty: Boolean
//        get() = details.eventType == EventType.EMPTY
//
//    override fun copy(): Event {
//        return Event(id, groupHeader, timestamp, room, details)
//    }
//
//    private fun requireEventsOfDay(): EventsOfDay {
//        return eventsOfDay
//            ?: throw IllegalStateException("Event isn't attached to the events of day")
//    }
//
//    val order: Int
//        get() = requireEventsOfDay().orderOf(this)
//
//    val eventType: EventType = details.eventType
//
//    companion object {
//
//        fun createEmpty(
//            id: String = UUIDS.createShort(),
//            groupHeader: GroupHeader,
//            details: EventDetails = EmptyEventDetails()
//        ): Event {
//            return Event(id, groupHeader, details = details)
//        }
//    }
//}