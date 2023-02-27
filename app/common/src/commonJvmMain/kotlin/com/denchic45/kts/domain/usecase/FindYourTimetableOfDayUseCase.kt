package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableOfDayResponse
import java.time.LocalDate
import javax.inject.Inject

class FindYourTimetableOfDayUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(date: LocalDate): Resource<TimetableOfDayResponse> {
        return eventRepository.findEventOfDayByMeAndDate(date)

    }
}