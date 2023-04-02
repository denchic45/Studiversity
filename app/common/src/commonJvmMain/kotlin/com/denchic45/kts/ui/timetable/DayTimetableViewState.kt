package com.denchic45.kts.ui.timetable

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
    val isEdit: Boolean = false
) {
    val title = getMonthTitle(date)
}

fun List<PeriodResponse>.toTimetableViewState(
    date: LocalDate,
    bellSchedule: BellSchedule,
): DayTimetableViewState {
    val latestEventOrder = max(maxOf { last().order }, 6)
    return DayTimetableViewState(
        date = date,
        periods = toItems(this, latestEventOrder),
        orders = bellSchedule.toItemOrders(latestEventOrder),
        maxEventsSize = latestEventOrder
    )
}

fun DayTimetableViewState.update(bellSchedule: BellSchedule): DayTimetableViewState {
    return copy(orders = bellSchedule.toItemOrders(maxEventsSize))
}

fun DayTimetableViewState.update(periodsOfDay: List<PeriodItem?>) = copy(periods = periodsOfDay)