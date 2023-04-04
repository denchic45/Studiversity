package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.uuidOfMe
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourTimetableByUseCase(private val eventRepository: EventRepository) {

    suspend operator fun invoke(weekOfYear: String): Resource<TimetableResponse> {
        return eventRepository.findTimetable(
            weekOfYear,
            memberIds = listOf(uuidOfMe())
        )
    }
}