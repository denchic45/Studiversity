package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Inject

@Inject
class PutTimetableUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        putTimetableRequest: PutTimetableRequest
    ): Resource<TimetableResponse> {
        return eventRepository.putTimetable(weekOfYear, putTimetableRequest)
    }
}