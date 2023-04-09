package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.uuidOf
import com.denchic45.stuiversity.util.uuidOrMe
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindTimetableOfWeekUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        owner: TimetableOwner2,
    ): Resource<TimetableResponse> = when (owner) {
      is  TimetableOwner2.Member -> {
            eventRepository.findTimetable(weekOfYear, memberIds = listOf(uuidOrMe(owner.ownerId)))
        }
      is  TimetableOwner2.StudyGroup -> {
            eventRepository.findTimetable(weekOfYear, studyGroupIds = listOf(owner.ownerId))
        }
      is  TimetableOwner2.Course -> {
            eventRepository.findTimetable(weekOfYear, courseIds = listOf(owner.ownerId))
        }
       is TimetableOwner2.Room -> {
            eventRepository.findTimetable(weekOfYear, roomIds = listOf(owner.ownerId))
        }
    }
}

enum class TimetableOwner { Member, StudyGroup, Course, Room }

sealed class TimetableOwner2 {
    abstract val ownerId: UUID?

    class Member(override val ownerId: UUID?) : TimetableOwner2()

    class StudyGroup(override val ownerId: UUID) : TimetableOwner2()

    class Course(override val ownerId: UUID) : TimetableOwner2()

    class Room(override val ownerId: UUID) : TimetableOwner2()
}
