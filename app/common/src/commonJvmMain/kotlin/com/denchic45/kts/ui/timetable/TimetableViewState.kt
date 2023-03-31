package com.denchic45.kts.ui.timetable

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.util.capitalized
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.toString
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

data class TimetableViewState(
    val mondayDate: LocalDate,
    val periods: List<List<Cell>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
    val isEdit: Boolean = false
) {

    val title = getMonthTitle(mondayDate)

    private fun getMonthTitle(monday: LocalDate): String {
        val saturday: LocalDate = monday.plusDays(5)
        return if (monday.monthValue != saturday.monthValue) {
            if (monday.year != saturday.year) {

                "${monday.toString("LLL yy").capitalized().replace(".", "")} - ${
                    saturday.toString("LLL yy").replace(".", "")
                }"
            } else {
                "${
                    (monday.toString("LLL").replace(".", "")).capitalized()
                } - ${saturday.toString("LLL").replace(".", "")}"
            }
        } else {
            monday.toString("LLLL").capitalized()
        }
    }

    sealed class Cell {
        data class Event(val iconName: String, val name: String, val room: RoomResponse?) : Cell()
        object Empty : Cell()
    }

    data class CellOrder(val order: Int, val time: String)
}

fun TimetableResponse.toTimetableViewState(
    latestEventOrder: Int,
    bellSchedule: BellSchedule,
) = TimetableViewState(
    mondayDate = LocalDate.parse(
        weekOfYear, DateTimeFormatterBuilder()
            .appendPattern("YYYY_ww")
            .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            .toFormatter()
    ),
    periods = toCellItems(this, latestEventOrder),
    orders = buildList {
        bellSchedule.schedule.take(latestEventOrder)
            .forEachIndexed { index, period ->
                add(TimetableViewState.CellOrder(index + 1, period.first))
            }
    },
    maxEventsSize = latestEventOrder
)

private fun toCellItems(
    timetable: TimetableResponse,
    latestPeriodOrder: Int
): List<List<TimetableViewState.Cell>> {
    return buildList {
        var lastOrder = 0
        timetable.days.forEach { periods ->
            add(buildList {
                periods.forEach { period ->
                    val diffOrders = period.order - lastOrder
                    if (diffOrders > 1) {
                        repeat(diffOrders) { add(TimetableViewState.Cell.Empty) }
                    } else {
                        add(
                            when (val details = period.details) {
                                is LessonDetails -> TimetableViewState.Cell.Event(
                                    details.subject.iconName,
                                    details.subject.name,
                                    period.room
                                )
                                is EventDetails -> TimetableViewState.Cell.Event(
                                    details.iconUrl,
                                    details.name,
                                    period.room
                                )
                            }
                        )
                    }
                    lastOrder++
                }
            })
        }
    }
}
