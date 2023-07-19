package com.denchic45.studiversity.feature.timetable.usecase

import com.denchic45.studiversity.feature.timetable.TimetableRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.timetable.model.PeriodsSorting
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class FindTimetableOfDayUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val timetableRepository: TimetableRepository
) {
  suspend operator fun invoke(
        studyGroupIds: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        weekOfYear: String,
        dayOfWeek: Int,
        sorting: List<PeriodsSorting>?
    ) = suspendTransactionWorker {
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