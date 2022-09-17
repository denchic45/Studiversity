package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.model.EmptyEventDetails
import com.denchic45.kts.domain.model.Lesson
import com.denchic45.kts.domain.model.SimpleEventDetails
import com.denchic45.kts.domain.usecase.FindEventsOfWeekByThisUserUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
import java.time.LocalDate

@Inject
class TimetableComponent(
    findEventsOfWeekByThisUserUseCase: FindEventsOfWeekByThisUserUseCase,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val coroutineScope = componentScope()

    val timetable = findEventsOfWeekByThisUserUseCase(LocalDate.now()
        .with(DayOfWeek.MONDAY)).map { eventsOfDays ->
        val maxEventsSize = eventsOfDays.maxOf { it.events.size }.let {
            if (it != 0) it
            else 8
        }
        val hasZeroEvents = eventsOfDays.any { it.startsAtZero }
        TimetableViewState(
            events = eventsOfDays.map { eventOfDay ->
                MutableList(maxEventsSize) {
                    if (it >= eventOfDay.size) {
                        return@MutableList TimetableViewState.Cell.Empty
                    }
                    val event = eventOfDay.events[it]
                    when (val details = event.details) {
                        is Lesson -> {
                            TimetableViewState.Cell.Event(details.subject.iconUrl,
                                details.subject.name,
                                event.room)
                        }
                        is SimpleEventDetails -> {
                            TimetableViewState.Cell.Event(details.iconUrl, details.name, event.room)
                        }
                        is EmptyEventDetails -> TimetableViewState.Cell.Empty
                    }
                }.apply {
                    if (hasZeroEvents && !eventOfDay.startsAtZero)
                        set(0, TimetableViewState.Cell.Empty)
                }
            },
            orders = MutableList(maxEventsSize) {
                TimetableViewState.CellOrder(it + 1, "")
            }.apply {
                if (hasZeroEvents)
                    set(0, TimetableViewState.CellOrder(0, ""))
            },
            maxEventsSize
        )
    }
}

data class TimetableViewState(
    val events: List<List<Cell>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
) {
    sealed class Cell {
        data class Event(val iconName: String, val name: String, val room: String) : Cell()
        object Empty : Cell()
    }

    class CellOrder(order: Int, time: String)
}