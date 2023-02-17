package com.studiversity.feature.timetable.usecase

import com.studiversity.feature.timetable.TimetableRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class PutTimetableUseCase(
    private val transactionWorker: TransactionWorker,
    private val timetableRepository: TimetableRepository
) {
    operator fun invoke(weekOfYear: String, putTimetableRequest: PutTimetableRequest): TimetableResponse {
        return transactionWorker {
            val monday = LocalDate.parse(
                weekOfYear, DateTimeFormatterBuilder()
                    .appendPattern("YYYY_ww")
                    .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
                    .toFormatter()
            )
            val studyGroupId = putTimetableRequest.studyGroupId
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(0), putTimetableRequest.monday)
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(1), putTimetableRequest.tuesday)
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(2), putTimetableRequest.wednesday)
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(3), putTimetableRequest.thursday)
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(4), putTimetableRequest.friday)
            timetableRepository.putPeriodsOfDay(studyGroupId, monday.plusDays(5), putTimetableRequest.saturday)

            timetableRepository.findByDateRange(monday, monday.plusDays(5), listOf(studyGroupId))
        }
    }
}