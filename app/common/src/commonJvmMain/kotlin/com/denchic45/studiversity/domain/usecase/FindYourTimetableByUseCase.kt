package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.EventRepository
import com.denchic45.studiversity.domain.Resource
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