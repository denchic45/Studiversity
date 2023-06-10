package com.denchic45.studiversity.ui.timetable.state

import androidx.compose.runtime.Immutable
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import kotlin.math.max

@Immutable
data class TimetableState(
    val firstWeekDate: LocalDate,
    private val periods: List<List<PeriodItem?>>,
    val orders: List<CellOrder>,
    val maxWeekEventsOrder: Int,
    val isEdit: Boolean = false,
) {

//    val title = getMonthTitle(firstWeekDate)

    val timetable = periods

    fun getDay(dayOfWeek: DayOfWeek): List<PeriodItem?> {
        return if (dayOfWeek.value == 7) emptyList()
        else periods[dayOfWeek.ordinal].dropLastWhile { it == null }
    }

    private val lastWeekDate = firstWeekDate.plusDays(6)
    private val yearWeek = firstWeekDate.get(WeekFields.ISO.weekOfWeekBasedYear())

    fun contains(selectedDate: LocalDate): Boolean {
        return yearWeek == selectedDate.get(WeekFields.ISO.weekOfWeekBasedYear())
    }
}

fun List<List<PeriodResponse>>.toTimetableState(
    yearWeek: String,
    bellSchedule: BellSchedule,
    isEdit: Boolean = false,
): TimetableState {
    val latestEventOrder = max(maxOf { it.size }, 6)
    return TimetableState(
        firstWeekDate = LocalDate.parse(
            yearWeek, DateTimeFormatterBuilder()
                .appendPattern("YYYY_ww")
                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                .toFormatter()
        ),
        periods = toItems(latestEventOrder),
        orders = bellSchedule.toItemOrders(latestEventOrder),
        maxWeekEventsOrder = latestEventOrder,
        isEdit = isEdit
    )
}

private fun List<List<PeriodResponse>>.toItems(latestPeriodOrder: Int): List<List<PeriodItem?>> =
    buildList {
        this@toItems.forEach { periods ->
            add(periods.toPeriodItems().let {
                it + List(latestPeriodOrder - periods.size) { null }
            })
        }
    }