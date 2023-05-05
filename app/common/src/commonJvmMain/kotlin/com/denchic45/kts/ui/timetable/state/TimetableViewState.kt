package com.denchic45.kts.ui.timetable.state

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.model.StudyGroupNameItem
import com.denchic45.kts.domain.timetable.model.PeriodDetails
import com.denchic45.kts.domain.timetable.model.PeriodItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.util.capitalized
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.util.toString
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

sealed class Cell {
    data class Event(val iconName: String?, val name: String?, val room: RoomResponse?) : Cell()
    object Empty : Cell()
}

data class CellOrder(val order: Int, val time: String)

fun toCells(
    periods: List<PeriodResponse>,
    latestPeriodOrder: Int,
) = buildList {
    periods.forEachIndexed { index, period ->
        val diffOrders = period.order - index
        if (diffOrders > 1) {
            repeat(diffOrders) { add(Cell.Empty) }
        } else {
            add(period.toCell())
        }
    }

    val diffOrders = latestPeriodOrder - periods.size
    if (diffOrders > 0) {
        repeat(diffOrders) { add(Cell.Empty) }
    }
}


private fun PeriodResponse.toCell() = when (val details = details) {
    is LessonDetails -> Cell.Event(
        details.course.subject?.iconUrl,
        details.course.subject?.name,
        room
    )
    is EventDetails -> Cell.Event(
        details.iconUrl,
        details.name,
        room
    )
}

fun toItemsForDay(periods: List<PeriodResponse>): List<PeriodItem?> {
    return periods.toItemsForWeek()
}

fun toItemsForWeek(
    periods: List<PeriodResponse>,
    latestPeriodOrder: Int,
): List<PeriodItem?> = buildList {
    addAll(periods.toItemsForWeek())
    val diffOrders = latestPeriodOrder - periods.size
    if (diffOrders > 0) {
        repeat(diffOrders) { add(null) }
    }
}

private fun List<PeriodResponse>.toItemsForWeek() = buildList {
    this@toItemsForWeek.forEachIndexed { index, period ->
        val diffOrders = period.order - index
        if (diffOrders > 1) {
            repeat(diffOrders) { add(null) }
        } else {
            add(period.toItem())
        }
    }
}

private fun PeriodResponse.toItem() = PeriodItem(
    id = id,
    studyGroup = StudyGroupNameItem(studyGroup.id, studyGroup.name),
    room = room?.name,
    members = members.map { it.toUserItem() },
    order = order,
    details = when (val details = details) {
        is EventDetails -> with(details) {
            PeriodDetails.Event(name, iconUrl, color)
        }
        is LessonDetails -> with(details) {
            PeriodDetails.Lesson(course.id, course.subject?.iconUrl, course.subject?.name)
        }
    }
)

fun BellSchedule.toItemOrders(
    latestEventOrder: Int,
) = buildList {
    periods.take(latestEventOrder)
        .forEachIndexed { index, period ->
            add(CellOrder(index + 1, period.start))
        }
    val diff = latestEventOrder - periods.size
    if (diff > 0) {
        val preLastPeriod = periods[periods.size - 2]
        val lastPeriod = periods.last()
        val startTimeOfLastPeriod =
            LocalTime.parse(lastPeriod.start, DateTimeFormatter.ofPattern("HH:mm"))
        val endTimeOfLastPeriod =
            LocalTime.parse(lastPeriod.start, DateTimeFormatter.ofPattern("HH:mm"))
        val endTimeOfPreLastPeriod =
            LocalTime.parse(preLastPeriod.end, DateTimeFormatter.ofPattern("HH:mm"))

        val breakTime = startTimeOfLastPeriod.minusHours(endTimeOfPreLastPeriod.hour.toLong())
            .minusMinutes(endTimeOfPreLastPeriod.minute.toLong())
        val periodTime = endTimeOfLastPeriod.minusHours(startTimeOfLastPeriod.hour.toLong())
            .minusMinutes(startTimeOfLastPeriod.minute.toLong())

        repeat(diff) {
            val lastCell: CellOrder = last()
            val lastTime = LocalTime.parse(lastCell.time, DateTimeFormatter.ofPattern("HH:mm"))
            val time = lastTime.plusMinutes(breakTime.minute.toLong() + periodTime.minute.toLong())
            add(CellOrder(lastCell.order + 1, time.format(DateTimeFormatter.ofPattern("HH:mm"))))
        }
    }
}

fun getMonthTitle(date: LocalDate): String {
    val saturday: LocalDate = date.plusDays(5)
    return if (date.monthValue != saturday.monthValue) {
        if (date.year != saturday.year) {
            "${date.toString("MMMM yyyy").capitalized().replace(".", "")} - ${
                saturday.toString("MMMM yyyy").replace(".", "")
            }"
        } else {
            "${
                (date.toString("MMMM").replace(".", "")).capitalized()
            } - ${saturday.toString("MMMM").replace(".", "")}"
        }
    } else {
        date.toString("MMMM").capitalized()
    }
}

fun String.toLocalDateOfWeekOfYear() = LocalDate.parse(
    this, DateTimeFormatterBuilder()
        .appendPattern("YYYY_ww")
        .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
        .toFormatter()
)