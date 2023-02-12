package com.studiversity.feature.timetable.usecase

import com.studiversity.feature.timetable.TimetableRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.timetable.model.PutTimetableOfDayRequest
import com.stuiversity.api.timetable.model.TimetableOfDayResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder

class PutTimetableOfDayUseCase(
    private val transactionWorker: TransactionWorker,
    private val timetableRepository: TimetableRepository
) {
    operator fun invoke(
        weekOfYear: String,
        dayOfWeek: Int,
        putTimetableOfDayRequest: PutTimetableOfDayRequest
    ): TimetableOfDayResponse {
        return transactionWorker {
            val studyGroupId = putTimetableOfDayRequest.studyGroupId
            val date = LocalDate.parse(
                "${weekOfYear}_$dayOfWeek", DateTimeFormatterBuilder()
                    .appendPattern("YYYY_ww_e")
                    .toFormatter()
            )
            timetableRepository.putPeriodsOfDay(
                studyGroupId = studyGroupId,
                date = date, periods = putTimetableOfDayRequest.periods
            )

            timetableRepository.findByDate(date, listOf(studyGroupId))
        }
    }
}