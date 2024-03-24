package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.TimetableRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.userIdOrMe
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindTimetableOfWeekUseCase(private val timetableRepository: TimetableRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        owner: TimetableOwner,
    ): Resource<TimetableResponse> = when (owner) {
        is TimetableOwner.Member -> {
            timetableRepository.findTimetable(
                weekOfYear,
                memberIds = listOf(userIdOrMe(owner.ownerId))
            )
        }

        is TimetableOwner.StudyGroup -> {
            timetableRepository.findTimetable(weekOfYear, studyGroupIds = listOf(owner.ownerId))
        }

        is TimetableOwner.Course -> {
            timetableRepository.findTimetable(weekOfYear, courseIds = listOf(owner.ownerId))
        }

        is TimetableOwner.Room -> {
            timetableRepository.findTimetable(weekOfYear, roomIds = listOf(owner.ownerId))
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