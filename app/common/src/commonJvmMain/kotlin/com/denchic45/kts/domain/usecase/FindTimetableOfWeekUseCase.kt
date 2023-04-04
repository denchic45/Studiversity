package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.uuidOf
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindTimetableOfWeekUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        owner: TimetableOwner,
        ownerId: UUID
    ): Resource<TimetableResponse> = when (owner) {
        TimetableOwner.Member -> {
            eventRepository.findTimetable(weekOfYear, memberIds = listOf(uuidOf(ownerId)))
        }
        TimetableOwner.StudyGroup -> {
            eventRepository.findTimetable(weekOfYear, studyGroupIds = listOf(ownerId))
        }
        TimetableOwner.Course -> {
            eventRepository.findTimetable(weekOfYear, courseIds = listOf(ownerId))
        }
        TimetableOwner.Room -> {
            eventRepository.findTimetable(weekOfYear, roomIds = listOf(ownerId))
        }
    }
}

enum class TimetableOwner { Member, StudyGroup, Course, Room }