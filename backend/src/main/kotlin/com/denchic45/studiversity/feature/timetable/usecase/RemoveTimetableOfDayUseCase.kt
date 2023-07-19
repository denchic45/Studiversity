package com.denchic45.studiversity.feature.timetable.usecase

import com.denchic45.studiversity.feature.timetable.TimetableRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class RemoveTimetableOfDayUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val timetableRepository: TimetableRepository
) {
  suspend operator fun invoke(
        studyGroupId: UUID,
        weekOfYear: String,
        dayOfWeek: Int
    ) = suspendTransactionWorker {
        timetableRepository.removeByStudyGroupIdAndDate(
            studyGroupId = studyGroupId,
            date = LocalDate.parse(
                "${weekOfYear}_$dayOfWeek", DateTimeFormatterBuilder()
                    .appendPattern("YYYY_ww_e")
                    .toFormatter()
            ),
        )
    }
}