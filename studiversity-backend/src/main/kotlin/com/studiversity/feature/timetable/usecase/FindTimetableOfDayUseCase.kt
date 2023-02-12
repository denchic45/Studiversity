package com.studiversity.feature.timetable.usecase

import com.studiversity.feature.timetable.TimetableRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.timetable.model.SortingPeriods
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class FindTimetableOfDayUseCase(
    private val transactionWorker: TransactionWorker,
    private val timetableRepository: TimetableRepository
) {
    operator fun invoke(
        studyGroupIds: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        weekOfYear: String,
        dayOfWeek: Int,
        sorting: List<SortingPeriods>?
    ) = transactionWorker {
        timetableRepository.findByDate(
            date = LocalDate.parse(
                "${weekOfYear}_$dayOfWeek", DateTimeFormatterBuilder()
                    .appendPattern("YYYY_ww_e")
                    .toFormatter()
            ),
            studyGroupId = studyGroupIds,
            memberIds = memberIds,
            courseIds = courseIds,
            roomIds = roomIds,
            sorting = sorting
        )
    }
}