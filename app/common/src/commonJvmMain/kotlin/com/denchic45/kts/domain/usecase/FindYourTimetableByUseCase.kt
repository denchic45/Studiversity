package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourTimetableByUseCase(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) {

    suspend operator fun invoke(weekOfYear: String): Resource<TimetableResponse> {
        return eventRepository.findTimetable(
            weekOfYear,
            memberIds = listOf(userRepository.findSelf().id)
        )
    }
}