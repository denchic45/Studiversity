package com.denchic45.studiversity.ui.timetable.state

import androidx.compose.runtime.Immutable
import com.denchic45.studiversity.data.service.model.BellPeriod
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.domain.timetable.model.Window
import com.denchic45.studiversity.util.copy
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields

@Immutable
data class TimetableState(
    val firstWeekDate: LocalDate,
    val dayTimetables: List<List<PeriodSlot>>,
    private val bellSchedule: BellSchedule,
//    val isEdit: Boolean = false,
    val showStudyGroups: Boolean = false
) {

    fun getByDay(dayOfWeek: DayOfWeek): List<PeriodSlot> {
        return if (dayOfWeek.value == 7) emptyList()
        else dayTimetables[dayOfWeek.ordinal].dropLastWhile { it is Window }
    }

    fun getOrderTime(position: Int): BellPeriod? {
        val periods = bellSchedule.periods
        return if (position >= periods.size) null
        else periods[position]
    }

    private val lastWeekDate = firstWeekDate.plusDays(6)
    private val yearWeek = firstWeekDate.get(WeekFields.ISO.weekOfWeekBasedYear())

    val maxEventsOfWeek = dayTimetables.maxOf(List<PeriodSlot>::size)

    fun contains(selectedDate: LocalDate): Boolean {
        return yearWeek == selectedDate.get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    fun addPeriod(dayOfWeek: DayOfWeek, period: PeriodItem): TimetableState {
        return copy(
            dayTimetables = dayTimetables.copy {
                this[dayOfWeek.ordinal] = this[dayOfWeek.ordinal] + period
            }
        )
    }

    fun updatePeriod(
        dayOfWeek: DayOfWeek,
        period: PeriodItem,
        position: Int
    ): TimetableState {
        return copy(
            dayTimetables = dayTimetables.copy {
                this[dayOfWeek.ordinal] = this[dayOfWeek.ordinal].copy {
                    this[position] = period
                }
            }
        )
    }

    fun removePeriod(dayOfWeek: DayOfWeek, position: Int): TimetableState {
        return copy(
            dayTimetables = dayTimetables.copy {
                this[dayOfWeek.ordinal] = this[dayOfWeek.ordinal].copy {
                    removeAt(position)
                }
            }
        )
    }
}

fun TimetableResponse.toTimetableState(
    yearWeek: String,
    bellSchedule: BellSchedule,
    showStudyGroups: Boolean = false
): TimetableState {
    return TimetableState(
        firstWeekDate = LocalDate.parse(
            yearWeek, DateTimeFormatterBuilder()
                .appendPattern("YYYY_ww")
                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                .toFormatter()
        ),
        dayTimetables = days.toItems(),
        bellSchedule = bellSchedule,
//        isEdit = isEdit,
        showStudyGroups = showStudyGroups
    )
}

private fun List<List<PeriodResponse>>.toItems(): List<List<PeriodSlot>> {
    return map(List<PeriodResponse>::toPeriodItems)
}

