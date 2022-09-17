package com.denchic45.kts.ui.timetable

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.model.EventsOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import java.time.LocalDate
import javax.inject.Inject

class TimetableInteractor @Inject constructor(
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository,
    private val groupPreferences: GroupPreferences,
    private val userPreferences: UserPreferences
) : Interactor {

    fun findEventsOfGroupByDate(date: LocalDate, groupId: String): Flow<EventsOfDay> {
        return eventRepository.findEventsOfDayByGroupIdAndDate(groupId, date)
    }

    val lessonTime: Int get() = eventRepository.lessonTime

    override fun removeListeners() {
        eventRepository.removeListeners()
        subjectRepository.removeListeners()
    }

    fun observeYourGroupId() = groupPreferences.observeGroupId.filter(String::isNotEmpty)

    val role: UserRole
        get() = UserRole.valueOf(userPreferences.role)

}