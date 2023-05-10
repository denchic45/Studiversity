package com.denchic45.kts.ui.timetable.state

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.timetable.model.PeriodItem
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import java.time.LocalDate
import kotlin.math.max

data class DayTimetableViewState(
    val date: LocalDate,
    val periods: List<PeriodItem?>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
    val isEdit: Boolean,
) {
    val title = getMonthTitle(date)

    companion object {
        fun create(
            schedule: BellSchedule,
            selected: LocalDate,
            periods: List<PeriodResponse>
        ): DayTimetableViewState? {
                val selectedDay = selected.dayOfWeek.ordinal
               return if (selectedDay == 6) null
                else periods.toDayTimetableViewState(
                    date = selected,
                    bellSchedule = schedule
                )
        }
    }
}

fun List<PeriodResponse>.toDayTimetableViewState(
    date: LocalDate,
    bellSchedule: BellSchedule,
    isEdit: Boolean = false,
): DayTimetableViewState {
    if (date.dayOfWeek.ordinal == 6) return emptyTimetable(date)

    val latestEventOrder = max(maxOfOrNull { last().order } ?: 0, 6)
    return DayTimetableViewState(
        date = date,
        periods = toPeriodItems(),
        orders = bellSchedule.toItemOrders(latestEventOrder),
        maxEventsSize = latestEventOrder,
        isEdit = isEdit
    )
}

fun DayTimetableViewState.update(bellSchedule: BellSchedule): DayTimetableViewState {
    return copy(orders = bellSchedule.toItemOrders(maxEventsSize))
}

fun DayTimetableViewState.update(periodsOfDay: List<PeriodItem?>) = copy(periods = periodsOfDay)

private fun emptyTimetable(date: LocalDate) = DayTimetableViewState(
    date = date,
    periods = emptyList(),
    orders = emptyList(),
    maxEventsSize = 0,
    isEdit = false
)