package com.denchic45.kts.domain.model

import com.denchic45.kts.util.UUIDS
import com.denchic45.kts.util.swap
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate

class EventsOfDay constructor(
    val date: LocalDate,
    events: List<Event> = listOf(),
    val startsAtZero: Boolean = false,
    val id: String,
) {

    val events: List<Event> = removeRedundantEmptyEvents(events.onEach { it.eventsOfDay = this })

    val size: Int = events.size

    val dayOfWeek: Int
        get() = date.dayOfWeek.value

    fun add(event: Event): EventsOfDay = copy(events.map(Event::copy) + event)

    fun add(event: Event, order: Int): EventsOfDay {
        return copy(
            events.map(Event::copy).toMutableList().apply { add(indexOfOrder(order), event) }
        )
    }

    fun update(editedEvent: Event, index: Int = orderOf(editedEvent)): EventsOfDay {
        return copy(events.toMutableList().apply {
            set(indexOfOrder(index), editedEvent)
        })
    }

    private fun indexOfOrder(order: Int): Int = order - offsetIndex

    private val offsetIndex: Int
        get() = if (startsAtZero) 0 else 1

    fun swap(oldIndex: Int, newIndex: Int): EventsOfDay {
        return copy(events.swap(oldIndex, newIndex).map(Event::copy))
    }

    private fun removeRedundantEmptyEvents(events: List<Event>): List<Event> {
        if (events.isEmpty()) return events
        val updatedEvents: MutableList<Event> = events.toMutableList()

        while (true) {
            if (updatedEvents.last().isEmpty) {
                updatedEvents.removeLast()
                if (updatedEvents.isEmpty())
                    break
            } else {
                break
            }
        }
        return updatedEvents
    }

    fun remove(event: Event): EventsOfDay {
        return copy(events.map(Event::copy) - event)
    }

    fun isEmpty(): Boolean = events.isEmpty()

    fun last(): Event? = events.lastOrNull()

    fun orderOf(event: Event): Int {
        return events.indexOfFirst { event.id == it.id } + offsetIndex
    }

    val weekName: String
        get() = date.toString("EEE")

    fun copy(events: List<Event>): EventsOfDay = EventsOfDay(date, events, startsAtZero, id)

    companion object {
        fun createEmpty(date: LocalDate): EventsOfDay =
            EventsOfDay(date, mutableListOf(), id = UUIDS.createShort())
    }
}