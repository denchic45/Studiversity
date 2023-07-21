package com.denchic45.studiversity.ui.timetable.state

import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.util.copy
import java.time.LocalDate

data class WeekTimetableViewState(
    val mondayDate: LocalDate,
    val periods: List<List<PeriodSlot>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
    val isEdit: Boolean = false,
)

//fun List<List<PeriodResponse>>.toDayTimetableViewState(
//    weekOfYear:String,
//    bellSchedule: BellSchedule,
//): WeekTimetableViewState {
//    val latestEventOrder = max(maxOf { it.size }, 6)
//    return WeekTimetableViewState(
//        mondayDate = LocalDate.parse(
//            weekOfYear, DateTimeFormatterBuilder()
//                .appendPattern("YYYY_ww")
//                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
//                .toFormatter()
//        ),
//        periods = toItemsForWeek(latestEventOrder),
//        orders = bellSchedule.toItemOrders(),
//        maxEventsSize = latestEventOrder
//    )
//}


// private fun List<List<PeriodResponse>>.toItemsForWeek(latestPeriodOrder:Int): List<List<PeriodSlot>> = buildList {
////    val latestPeriodOrder = max(maxOf { it.size }, 6)
//    this@toItemsForWeek.forEach { periods ->
//        add(periods.toPeriodItems().let {
//            it + List(latestPeriodOrder - size) { Window() }
//        })
//    }
//}

fun WeekTimetableViewState.update(bellSchedule: BellSchedule): WeekTimetableViewState {
    return copy(orders = bellSchedule.toItemOrders())
}

fun WeekTimetableViewState.update(
    dayOfWeek: Int,
    periodsOfDay: List<PeriodSlot>,
): WeekTimetableViewState = copy(periods = periods.copy { this[dayOfWeek] = periodsOfDay })
