package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.EventRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.uuidOrMe
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindTimetableOfWeekUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        owner: TimetableOwner,
    ): Resource<TimetableResponse> = when (owner) {
      is  TimetableOwner.Member -> {
            eventRepository.findTimetable(weekOfYear, memberIds = listOf(uuidOrMe(owner.ownerId)))
        }
      is  TimetableOwner.StudyGroup -> {
            eventRepository.findTimetable(weekOfYear, studyGroupIds = listOf(owner.ownerId))
        }
      is  TimetableOwner.Course -> {
            eventRepository.findTimetable(weekOfYear, courseIds = listOf(owner.ownerId))
        }
       is TimetableOwner.Room -> {
            eventRepository.findTimetable(weekOfYear, roomIds = listOf(owner.ownerId))
        }
    }
}

sealed class TimetableOwner {
    abstract val ownerId: UUID?

    class Member(override val ownerId: UUID?) : TimetableOwner()

    class StudyGroup(override val ownerId: UUID) : TimetableOwner()

    class Course(override val ownerId: UUID) : TimetableOwner()

    class Room(override val ownerId: UUID) : TimetableOwner()
}