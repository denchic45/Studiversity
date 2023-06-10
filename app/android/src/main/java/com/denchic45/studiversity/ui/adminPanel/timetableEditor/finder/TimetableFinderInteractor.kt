package com.denchic45.studiversity.ui.adminPanel.timetableEditor.finder


//import com.denchic45.studiversity.domain.model.EventsOfDay
//import com.denchic45.studiversity.data.prefs.AppPreference
//import com.denchic45.studiversity.data.repository.EventRepository
//import com.denchic45.studiversity.data.repository.StudyGroupRepository
//import kotlinx.coroutines.flow.Flow
//import java.time.LocalDate
//import javax.inject.Inject
//
//class TimetableFinderInteractor @Inject constructor(
//    private val studyGroupRepository: StudyGroupRepository,
//    private val eventRepository: EventRepository,
//    private val appPreference: AppPreference
//) {
//
//    fun findEventsOfDayByGroup(date: LocalDate, groupId: String): Flow<EventsOfDay> {
//        return eventRepository.findEventsOfDayByGroupIdAndDate(groupId, date)
//    }
//
//    val lessonTime: Int
//        get() = appPreference.lessonTime
//
//    suspend fun updateGroupEventsOfDay(eventsOfDay: EventsOfDay, groupHeader: GroupHeader) {
//        eventRepository.updateEventsOfDay(eventsOfDay, groupHeader)
//    }
//}