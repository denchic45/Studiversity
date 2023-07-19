package com.denchic45.studiversity.feature.timetable.usecase

import com.denchic45.studiversity.feature.timetable.TimetableRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.timetable.model.PeriodsSorting
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

class FindTimetableUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val timetableRepository: TimetableRepository
) {
  suspend operator fun invoke(
        studyGroupIds: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        weekOfYear: String,
        sorting: List<PeriodsSorting>?
    ) = suspendTransactionWorker {
        val monday = LocalDate.parse(
            weekOfYear, DateTimeFormatterBuilder()
                .appendPattern("YYYY_ww")
                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                .toFormatter()
        )

        timetableRepository.findByDateRange(
            startDate = monday,
            endDate = monday.plusDays(5),
            studyGroupId = studyGroupIds,
            memberIds = memberIds,
            courseIds = courseIds,
            roomIds = roomIds,
            sorting = sorting
        )
    }
}