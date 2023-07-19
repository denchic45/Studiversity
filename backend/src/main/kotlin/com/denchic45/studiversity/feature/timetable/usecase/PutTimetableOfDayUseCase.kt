package com.denchic45.studiversity.feature.timetable.usecase

import com.denchic45.studiversity.feature.timetable.TimetableRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.timetable.model.PutTimetableOfDayRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableOfDayResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder

class PutTimetableOfDayUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val timetableRepository: TimetableRepository
) {
  suspend operator fun invoke(
        weekOfYear: String,
        dayOfWeek: Int,
        putTimetableOfDayRequest: PutTimetableOfDayRequest
    ): TimetableOfDayResponse {
        return suspendTransactionWorker {
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