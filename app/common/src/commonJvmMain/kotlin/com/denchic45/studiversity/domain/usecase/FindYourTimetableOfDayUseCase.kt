package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.EventRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableOfDayResponse
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class FindYourTimetableOfDayUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(date: LocalDate): Resource<TimetableOfDayResponse> {
        return eventRepository.findEventOfDayByMeAndDate(date)

    }
}