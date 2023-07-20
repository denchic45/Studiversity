package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.CourseLocalDataSource
import com.denchic45.studiversity.data.db.local.source.DayLocalDataSource
import com.denchic45.studiversity.data.db.local.source.EventLocalDataSource
import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupLocalDataSource
import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.fetchObservingResource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.pref.UserPreferences
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.timetable.TimetableApi
import com.denchic45.stuiversity.api.timetable.model.PeriodsSorting
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.util.UUIDWrapper
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class TimetableRepository(
    override val networkService: NetworkService,
    private val eventLocalDataSource: EventLocalDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val dayLocalDataSource: DayLocalDataSource,
    private val userPreferences: UserPreferences,
    override val userLocalDataSource: UserLocalDataSource,
    override val studyGroupLocalDataSource: StudyGroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val timetableApi: TimetableApi,
) : NetworkServiceOwner, SaveGroupOperation {

    fun findEventsOfDay(
        date: LocalDate,
        memberIds: List<UUID>? = null,
        studyGroupIds: List<UUID>? = null,
    ) = fetchObservingResource {
        flow {
            emit(
                timetableApi.getTimetableOfDay(
                    date,
                    memberIds = memberIds,
                    studyGroupIds = studyGroupIds
                )
            )
        }
    }

    suspend fun findTimetable(
        weekOfYear: String,
        studyGroupIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        memberIds: List<UUIDWrapper>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting> = emptyList(),
    ) = fetchResource {
        timetableApi.getTimetable(
            weekOfYear = weekOfYear,
            studyGroupIds = studyGroupIds,
            courseIds = courseIds,
            memberIds = memberIds,
            roomIds = roomIds,
            sorting = sorting
        )
    }

//    fun findEventsOfDayByGroupIdAndDate(groupId: String, date: LocalDate): Flow<EventsOfDay> {
//        return flow {
//            coroutineScope {
//                launch {
//                    eventRemoteDataSource.observeEventsOfGroupByDate(groupId, date).filterNotNull()
//                        .collect { saveDay(it) }
//                }
//                emitAll(eventLocalDataSource.observeDayEventsByDateAndGroupId(date, groupId)
//                    .map { it?.entityToUserDomain() ?: EventsOfDay.createEmpty(date) })
//            }
//        }
//    }

//    fun findEventsOfDayByYourGroupAndDate(selectedDate: LocalDate): Flow<EventsOfDay> {
//        return groupPreferences.observeGroupId.filter(String::isNotEmpty).flatMapLatest { groupId ->
//            if (selectedDate.toDateUTC() > nextSaturday || selectedDate.toDateUTC() < previousMonday) {
//                findEventsOfDayByGroupIdAndDate(groupId, selectedDate)
//            } else {
//                eventLocalDataSource.observeDayEventsByDateAndGroupId(selectedDate, groupId)
//                    .map { it?.entityToUserDomain() ?: EventsOfDay.createEmpty(selectedDate) }
//                    .distinctUntilChanged()
//            }
//        }
//    }

//    fun findTimetableByYourGroupAndWeek(selectedMonday: LocalDate): Flow<List<EventsOfDay>> {
//        if (selectedMonday.dayOfWeek != DayOfWeek.MONDAY) throw IllegalArgumentException("Date must be only monday")
//        return groupPreferences.observeGroupId.filter(String::isNotEmpty).flatMapLatest { groupId ->
//            val dates = List(6) { selectedMonday.plusDays(it.toLong()) }
//            eventLocalDataSource.observeEventsByDateRangeAndGroupId(
//                groupId = groupId, dates = dates
//            ).map { daysWithEvents ->
//                daysWithEvents.zip(dates) { entity, date ->
//                    entity?.entityToUserDomain() ?: EventsOfDay.createEmpty(date)
//                }
//            }.distinctUntilChanged()
//        }
//    }

    suspend fun findEventOfDayByMeAndDate(date: LocalDate) = fetchResource {
        timetableApi.getTimetableOfDay(
            date,
            memberIds = listOf(userPreferences.id.toUUID())
        )
    }

//    private suspend fun saveDay(dayMap: DayMap) {
//        val notRelatedTeacherEntities = courseLocalDataSource.getNotRelatedTeacherIdsToGroup(
//            dayMap.teacherIds, dayMap.groupId
//        ).map { teacherId ->
//            userRemoteDataSource.findById(teacherId).mapToUser()
//        }
//
//        val notRelatedSubjectEntities = courseLocalDataSource.getNotRelatedSubjectIdsToGroup(
//            dayMap.subjectIds, dayMap.groupId
//        ).map { subjectId ->
//            subjectRemoteDataSource.findById(subjectId).mapToSubject()
//        }
//
//        val eventEntities = dayMap.events.map { EventMap(it).mapToEntity(dayMap.id) }
//
//        dayLocalDataSource.saveDay(
//            notRelatedTeacherEntities = notRelatedTeacherEntities,
//            notRelatedSubjectEntities = notRelatedSubjectEntities,
//            dayEntity = dayMap.mapToEntity(),
//            eventEntities = eventEntities,
//            teacherEventEntities = eventEntities.toTeacherEventEntities()
//        )
//    }

//    suspend fun observeEventsOfYourGroup() {
//        groupPreferences.observeGroupId.filter(String::isNotEmpty).flatMapLatest { groupId ->
//            eventRemoteDataSource.observeEventsOfGroupByPreviousAndNextDates(
//                groupId, previousMonday, nextSaturday
//            )
//        }.collect { dayMaps -> dayMaps.forEach { saveDay(it) } }
//    }


    suspend fun updateTimetableOfWeek(
        weekOfYear: String, putTimetableRequest: PutTimetableRequest
    ) = fetchResource {
        timetableApi.putTimetable(weekOfYear, putTimetableRequest)
    }
}