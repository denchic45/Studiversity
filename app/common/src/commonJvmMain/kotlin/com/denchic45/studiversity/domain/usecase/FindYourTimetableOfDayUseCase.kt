package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.EventRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableOfDayResponse
import java.time.LocalDate
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindYourTimetableOfDayUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(date: LocalDate): Resource<TimetableOfDayResponse> {
        return eventRepository.findEventOfDayByMeAndDate(date)

    }
}