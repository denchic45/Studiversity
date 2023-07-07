package com.denchic45.studiversity.ui.timetable.state

import androidx.compose.runtime.Immutable
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.domain.timetable.model.Window
import com.denchic45.studiversity.util.copy
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
    val timetable: List<List<PeriodSlot>>,
    private val orders: List<CellOrder>,
    val maxWeekEventsOrder: Int,
    val isEdit: Boolean = false,
    val showStudyGroups: Boolean = false
) {

//    val title = getMonthTitle(firstWeekDate)


    fun getDay(dayOfWeek: DayOfWeek): List<PeriodSlot> {
        return if (dayOfWeek.value == 7) emptyList()
        else timetable[dayOfWeek.ordinal].dropLastWhile { it is Window }
    }

    fun getOrder(position: Int): CellOrder? {
        return if (position >= orders.size) null
        else orders[position]
    }

    private val lastWeekDate = firstWeekDate.plusDays(6)
    private val yearWeek = firstWeekDate.get(WeekFields.ISO.weekOfWeekBasedYear())

    fun contains(selectedDate: LocalDate): Boolean {
        return yearWeek == selectedDate.get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    fun addPeriod(dayOfWeek: Int, period: PeriodResponse): TimetableState {
        return copy(
            timetable = timetable.copy {
                this[dayOfWeek] = this[dayOfWeek] + period.toItem()
            }
        )
    }

    fun updatePeriod(dayOfWeek: Int, period: PeriodResponse, position: Int): TimetableState {
        return copy(
            timetable = timetable.copy {
                this[dayOfWeek] = this[dayOfWeek].copy {
                    this[position] = period.toItem()
                }
            }
        )
    }

    fun removePeriod(dayOfWeek: Int, position: Int): TimetableState {
        return copy(
            timetable = timetable.copy {
                this[dayOfWeek] = this[dayOfWeek].copy {
                    removeAt(position)
                }
            }
        )
    }


    fun List<List<PeriodResponse>>.toTimetableState(
        yearWeek: String,
        bellSchedule: BellSchedule,
        isEdit: Boolean = false,
        showStudyGroups: Boolean = false
    ): TimetableState {
        val latestEventOrder = max(maxOf { it.lastOrNull()?.order ?: 0 }, 6)
        return TimetableState(
            firstWeekDate = LocalDate.parse(
                yearWeek, DateTimeFormatterBuilder()
                    .appendPattern("YYYY_ww")
                    .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                    .toFormatter()
            ),
            timetable = toItems(latestEventOrder),
            orders = bellSchedule.toItemOrders(latestEventOrder),
            maxWeekEventsOrder = latestEventOrder,
            isEdit = isEdit,
            showStudyGroups = showStudyGroups
        )
    }

    private fun List<List<PeriodResponse>>.toItems(latestPeriodOrder: Int): List<List<PeriodSlot>> =
        buildList {
            this@toItems.forEach { periods ->
                add(periods.toPeriodItems().let {
                    it + List(latestPeriodOrder - periods.size) { Window() }
                })
            }
        }
}
