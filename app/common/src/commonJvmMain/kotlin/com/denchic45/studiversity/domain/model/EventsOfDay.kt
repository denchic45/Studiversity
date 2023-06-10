package com.denchic45.studiversity.domain.model

import com.denchic45.studiversity.util.UUIDS
import com.denchic45.studiversity.util.swap
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate

class EventsOfDay constructor(
    val date: LocalDate,
    events: List<PeriodResponse> = listOf(),
    val id: String,
) {

    val events: List<PeriodResponse> =
//        removeRedundantEmptyEvents(events.onEach {  })
        events

    val size: Int = events.size

    val dayOfWeek: Int
        get() = date.dayOfWeek.value

    fun add(event: PeriodResponse): EventsOfDay = copy(events.map(PeriodResponse::copy) + event)

    fun add(event: PeriodResponse, order: Int): EventsOfDay {
        return copy(
            events.map(PeriodResponse::copy).toMutableList()
                .apply { add(indexOfOrder(order), event) }
        )
    }

    fun update(editedEvent: PeriodResponse, index: Int = orderOf(editedEvent)): EventsOfDay {
        return copy(events.toMutableList().apply {
            set(indexOfOrder(index), editedEvent)
        })
    }

    private fun indexOfOrder(order: Int): Int = order

//    private val offsetIndex: Int
//        get() = if (startsAtZero) 0 else 1

    fun swap(oldIndex: Int, newIndex: Int): EventsOfDay {
        return copy(events.swap(oldIndex, newIndex).map(PeriodResponse::copy))
    }

//    private fun removeRedundantEmptyEvents(events: List<PeriodResponse>): List<PeriodResponse> {
//        if (events.isEmpty()) return events
//        val updatedEvents: MutableList<PeriodResponse> = events.toMutableList()
//
//        while (true) {
//            if (updatedEvents.last().isEmpty) {
//                updatedEvents.removeLast()
//                if (updatedEvents.isEmpty())
//                    break
//            } else {
//                break
//            }
//        }
//        return updatedEvents
//    }

    fun remove(event: PeriodResponse): EventsOfDay {
        return copy(events.map(PeriodResponse::copy) - event)
    }

    fun isEmpty(): Boolean = events.isEmpty()

    fun last(): PeriodResponse? = events.lastOrNull()

    private fun orderOf(event: PeriodResponse): Int {
        return events.indexOfFirst { event.id == it.id }
    }

    val weekName: String
        get() = date.toString("EEE")

    fun copy(events: List<PeriodResponse>): EventsOfDay = EventsOfDay(date, events, id)

    companion object {
        fun createEmpty(date: LocalDate): EventsOfDay =
            EventsOfDay(date, mutableListOf(), id = UUIDS.createShort())
    }
}