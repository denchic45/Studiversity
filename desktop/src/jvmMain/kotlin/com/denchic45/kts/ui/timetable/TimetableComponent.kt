package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.model.EmptyEventDetails
import com.denchic45.kts.domain.model.Lesson
import com.denchic45.kts.domain.model.SimpleEventDetails
import com.denchic45.kts.domain.usecase.FindEventsOfWeekByThisUserUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
import java.time.LocalDate

@Inject
class TimetableComponent(
    findEventsOfWeekByThisUserUseCase: FindEventsOfWeekByThisUserUseCase,
    metaRepository: MetaRepository,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val coroutineScope = componentScope()

    private val currentWeek: LocalDate
        get() = LocalDate.now().with(DayOfWeek.MONDAY)

    private val selectedDate = MutableStateFlow(currentWeek)

    @OptIn(ExperimentalCoroutinesApi::class)
    val timetable =
        combine(selectedDate.flatMapLatest { date -> findEventsOfWeekByThisUserUseCase(date) },
            metaRepository.observeBellSchedule) { eventsOfDays, bellSchedule ->
            val hasZeroEvents = eventsOfDays.any { it.startsAtZero }
            val latestEventOrder = eventsOfDays.maxOf { it.last()?.order ?: 0 }.let {
                if (it != 0) it
                else 8
            }
            val maxEventsRange = latestEventOrder + if (hasZeroEvents) 1 else 0

            TimetableViewState(monday = selectedDate.value,
                events = eventsOfDays.map { eventOfDay ->
                    MutableList(maxEventsRange) {
                        if (it >= eventOfDay.size) {
                            return@MutableList TimetableViewState.Cell.Empty
                        }
                        val event = eventOfDay.events[it]
                        when (val details = event.details) {
                            is Lesson -> {
                                TimetableViewState.Cell.Event(details.subject.iconName,
                                    details.subject.name,
                                    event.room)
                            }
                            is SimpleEventDetails -> {
                                TimetableViewState.Cell.Event(details.iconUrl,
                                    details.name,
                                    event.room)
                            }
                            is EmptyEventDetails -> TimetableViewState.Cell.Empty
                        }
                    }.apply {
                        if (hasZeroEvents && !eventOfDay.startsAtZero) add(0,
                            TimetableViewState.Cell.Empty)
                    }
                },
                orders = buildList {
                    bellSchedule.schedule.take(latestEventOrder).forEachIndexed { index, period ->
                        add(TimetableViewState.CellOrder(index + 1, period.first))
                    }
                    if (hasZeroEvents) {
                        add(0,
                            TimetableViewState.CellOrder(0, bellSchedule.zeroPeriod?.first ?: "-"))
                    }
                },
                maxEventsSize = maxEventsRange)
        }

    fun onNextWeekClick() = selectedDate.update { it.plusWeeks(1) }

    fun onPreviousWeekClick() = selectedDate.update { it.minusWeeks(1) }

    fun onTodayClick() = selectedDate.update { currentWeek }

}

data class TimetableViewState(
    val monday: LocalDate,
    val events: List<List<Cell>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
) {
    sealed class Cell {
        data class Event(val iconName: String, val name: String, val room: String) : Cell()
        object Empty : Cell()
    }

    data class CellOrder(val order: Int, val time: String)
}