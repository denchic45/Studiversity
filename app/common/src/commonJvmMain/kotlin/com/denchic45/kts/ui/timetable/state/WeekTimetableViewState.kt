package com.denchic45.kts.ui.timetable.state

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.timetable.model.PeriodItem
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.math.max

data class WeekTimetableViewState(
    val mondayDate: LocalDate,
    val periods: List<List<PeriodItem?>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
    val isEdit: Boolean = false,
) {
    val title = getMonthTitle(mondayDate)
}

fun List<List<PeriodResponse>>.toDayTimetableViewState(
    weekOfYear:String,
    bellSchedule: BellSchedule,
): WeekTimetableViewState {
    val latestEventOrder = max(maxOf { it.size }, 6)
    return WeekTimetableViewState(
        mondayDate = LocalDate.parse(
            weekOfYear, DateTimeFormatterBuilder()
                .appendPattern("YYYY_ww")
                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                .toFormatter()
        ),
        periods = toItemsForWeek(latestEventOrder),
        orders = bellSchedule.toItemOrders(latestEventOrder),
        maxEventsSize = latestEventOrder
    )
}


 private fun List<List<PeriodResponse>>.toItemsForWeek(latestPeriodOrder:Int): List<List<PeriodItem?>> = buildList {
//    val latestPeriodOrder = max(maxOf { it.size }, 6)
    this@toItemsForWeek.forEach { periods ->
        add(periods.toPeriodItems().let {
            it + List(latestPeriodOrder - size) { null }
        })
    }
}

fun WeekTimetableViewState.update(bellSchedule: BellSchedule): WeekTimetableViewState {
    return copy(orders = bellSchedule.toItemOrders(maxEventsSize))
}

fun WeekTimetableViewState.update(
    dayOfWeek: Int,
    periodsOfDay: List<PeriodItem?>,
): WeekTimetableViewState = copy(periods = periods.copy { this[dayOfWeek] = periodsOfDay })
