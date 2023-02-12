package com.studiversity.feature.timetable.usecase

import com.studiversity.feature.timetable.TimetableRepository
import com.studiversity.transaction.TransactionWorker
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

class RemoveTimetableUseCase(
    private val transactionWorker: TransactionWorker,
    private val timetableRepository: TimetableRepository
) {
    operator fun invoke(
        studyGroupId: UUID,
        weekOfYear: String
    ) = transactionWorker {
        val monday = LocalDate.parse(
            weekOfYear, DateTimeFormatterBuilder()
                .appendPattern("YYYY_ww")
                .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                .toFormatter()
        )

        timetableRepository.removeByStudyGroupIdAndDateRange(
            studyGroupId = studyGroupId,
            startDate = monday,
            endDate = monday.plusDays(5),
        )
    }
}