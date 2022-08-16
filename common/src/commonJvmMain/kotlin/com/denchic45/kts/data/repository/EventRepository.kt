package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.DayEntity
import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.db.remote.model.DayMap
import com.denchic45.kts.data.db.remote.model.EventMap
import com.denchic45.kts.data.db.remote.source.EventRemoteDataSource
import com.denchic45.kts.data.db.remote.source.GroupRemoteDataSource
import com.denchic45.kts.data.db.remote.source.SubjectRemoteDataSource
import com.denchic45.kts.data.db.remote.source.UserRemoteDataSource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.EventsOfDay
import com.denchic45.kts.domain.model.GroupHeader
import com.denchic45.kts.domain.model.GroupTimetable
import com.denchic45.kts.util.DatePatterns
import com.denchic45.kts.util.NetworkException
import com.denchic45.kts.util.toDateUTC
import com.denchic45.kts.util.toString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import javax.inject.Inject

class EventRepository @Inject constructor(
    override val networkService: NetworkService,
    private val eventLocalDataSource: EventLocalDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val groupPreferences: GroupPreferences,
    private val dayLocalDataSource: DayLocalDataSource,
    private val userPreferences: UserPreferences,
    private val appPreferences: AppPreferences,
    override val appVersionService: AppVersionService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val eventRemoteDataSource: EventRemoteDataSource,
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val subjectRemoteDataSource: SubjectRemoteDataSource,
) : Repository(), SaveGroupOperation {

    fun findEventsOfDayByGroupIdAndDate(groupId: String, date: LocalDate): Flow<EventsOfDay> {
        return flow {
            coroutineScope {
                launch {
                    eventRemoteDataSource.observeEventsOfGroupByDate(groupId, date).collect {
                        saveDay(it)
                    }
                }
                emitAll(eventLocalDataSource.observeEventsByDateAndGroupId(date, groupId)
                    .map { it.entityToUserDomain() })
            }
        }
    }

    fun findEventsOfDayByYourGroupAndDate(selectedDate: LocalDate): Flow<EventsOfDay> {
        return groupPreferences.observeGroupId
            .filter(String::isNotEmpty)
            .flatMapLatest { groupId ->
                if (selectedDate.toDateUTC() > nextSaturday || selectedDate.toDateUTC() < previousMonday) {
                    findEventsOfDayByGroupIdAndDate(groupId, selectedDate)
                } else {
                    eventLocalDataSource.observeEventsByDateAndGroupId(selectedDate, groupId)
                        .map { it.entityToUserDomain() }
                        .distinctUntilChanged()
                }
            }
    }

    fun findEventsForDayForTeacherByDate(date: LocalDate): Flow<EventsOfDay> = callbackFlow {
        val teacherId = userPreferences.id
        launch {
            eventLocalDataSource.observeEventsByDateAndTeacherId(date, teacherId)
                .distinctUntilChanged()
                .map { it.entitiesToEventsOfDay(date) }
                .collect { send(it) }
        }

        eventRemoteDataSource.observeEventsOfTeacherByDate(teacherId, date).collect { dayMaps ->
            for (dayMap in dayMaps) {
                if (!groupLocalDataSource.isExist(dayMap.groupId)) {
                    saveGroup(groupRemoteDataSource.findById(dayMap.groupId))
                }
                saveDay(dayMap)
            }
        }
    }

    private suspend fun saveDay(dayMap: DayMap) {
        val notRelatedTeacherEntities =
            courseLocalDataSource.getNotRelatedTeacherIdsToGroup(
                dayMap.teacherIds,
                dayMap.groupId
            ).map { teacherId ->
                userRemoteDataSource.findById(teacherId).mapToUserEntity()
            }

        val notRelatedSubjectEntities =
            courseLocalDataSource.getNotRelatedSubjectIdsToGroup(
                dayMap.subjectIds,
                dayMap.groupId
            ).map { subjectId ->
                subjectRemoteDataSource.findById(subjectId).mapToSubjectEntity()
            }

        val eventEntities = dayMap.events.map { EventMap(it).mapToEntity(dayMap.id) }
//                eventLocalDataSource.upsert(eventEntities)

//                teacherEventLocalDataSource.insert(eventEntities.toTeacherEventEntities())

        dayLocalDataSource.saveDay(
            notRelatedTeacherEntities = notRelatedTeacherEntities,
            notRelatedSubjectEntities = notRelatedSubjectEntities,
            dayEntity = dayMap.mapToEntity(),
            eventEntities = eventEntities,
            teacherEventEntities = eventEntities.toTeacherEventEntities()
        )
    }

//    private fun eventsOfTeacherByDate(
//        date: LocalDate,
//        teacherId: String,
//    ): ListenerRegistration {
//        return firestore.collectionGroup("Days")
//            .whereArrayContains("teacherIds", teacherId)
//            .whereEqualTo("date", date.toDateUTC())
//            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
//                if (error != null) {
//                    Log.d("lol", "getLessonsOfTeacherByDateListener: ", error)
//                    return@addSnapshotListener
//                }
//                if (!snapshot!!.isEmpty) {
//                    val dayMaps = snapshot.toMutableMaps(::DayMap)
//                    for (dayMap in dayMaps) {
//                        coroutineScope.launch(dispatcher) {
//                            if (!groupLocalDataSource.isExist(dayMap.groupId)) {
//                                val documentSnapshot = groupsRef.document(dayMap.groupId)
//                                    .get()
//                                    .await()
//                                if (documentSnapshot.exists())
//                                    saveGroup(GroupMap(documentSnapshot.toMap()))
//                            }
//                            saveDay(dayMap)
//                        }
//                    }
//                }
//            }
//    }

    var lessonTime: Int
        get() = appPreferences.lessonTime
        set(lessonTime) {
            appPreferences.lessonTime = lessonTime
        }

    suspend fun observeEventsOfYourGroup() {
        eventRemoteDataSource.observeEventsOfGroupByPreviousAndAfterDates(
            groupPreferences.groupId,
            previousMonday,
            nextSaturday
        ).collect { dayMaps -> dayMaps.forEach { saveDay(it) } }
    }

    private val nextSaturday: Date
        get() = LocalDate.now()
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            .plusWeeks(1)
            .toDateUTC()

    private val previousMonday: Date
        get() = LocalDate.now()
            .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
            .minusWeeks(1)
            .toDateUTC()

    suspend fun addGroupTimetables(groupTimetables: List<GroupTimetable>) {
        if (isNetworkNotAvailable) throw NetworkException()
        groupTimetables.map { addGroupTimetable(it) }
    }

    private suspend fun addGroupTimetable(
        groupTimetable: GroupTimetable,
    ) {
        val groupWeekEvents = groupTimetable.weekEvents
//        val dayRef = groupsRef.document(groupTimetable.groupHeader.id).collection("Days")

        eventLocalDataSource.deleteByGroupAndDateRange(
            groupTimetable.groupHeader.id,
            groupWeekEvents[0].date,
            groupWeekEvents[5].date
        )

        val existsDayMaps: List<DayMap> = eventRemoteDataSource.findEventsOfGroupByDateRange(
            groupId = groupTimetable.groupHeader.id,
            previousMonday = groupTimetable.weekEvents[0].date.toDateUTC(),
            nextSaturday = groupTimetable.weekEvents[5].date.toDateUTC()
        )

        for (eventsOfTheDay in groupWeekEvents) {
            val maybeDayMap = findDayByDate(existsDayMaps, eventsOfTheDay.date.toDateUTC())

            val addableEvents = eventsOfTheDay.events.map { it.domainToMap() }

            val dayMap: DayMap = maybeDayMap?.let {
                it.events = addableEvents
                it
            } ?: DayMap(eventsOfTheDay.domainToMap(groupTimetable.groupHeader.id))

            eventRemoteDataSource.setDay(dayMap)

            dayLocalDataSource.upsert(
                DayEntity(
                    day_id = dayMap.id,
                    date = dayMap.date.toString(DatePatterns.yyy_MM_dd),
                    start_at_zero = eventsOfTheDay.startsAtZero,
                    group_id = groupTimetable.groupHeader.id
                )
            )
        }

    }

    private fun findDayByDate(dayDocs: List<DayMap>, date: Date): DayMap? {
        return dayDocs.firstOrNull { dayDoc -> dayDoc.date == date }
    }

//    private fun getQueryOfWeekDays(
//        groupTimetable: GroupTimetable,
//        daysRef: CollectionReference,
//    ): Task<QuerySnapshot> {
//        val monday = groupTimetable.weekEvents[0].date.toDateUTC()
//        val saturday = groupTimetable.weekEvents[5].date.toDateUTC()
//        return daysRef.whereGreaterThanOrEqualTo("date", monday)
//            .whereLessThanOrEqualTo("date", saturday)
//            .get()
//    }


    suspend fun updateEventsOfDay(updatedEventsOfDay: EventsOfDay, groupHeader: GroupHeader) {
        val groupId = groupHeader.id
        val localDayId =
            dayLocalDataSource.getIdByDateAndGroupId(updatedEventsOfDay.date, groupId)
        if (isNetworkNotAvailable) return
        if (localDayId != null) {
//            val remotedDayMap = eventRemoteDataSource.findEventsOfGroupByDate(
//                groupHeader.id, updatedEventsOfDay.date
//            )
//            dayLocalDataSource.upsert(remotedDayMap.mapToEntity())
//            if (remotedDayMap.id != localDayId)
//                dayLocalDataSource.deleteById(localDayId)

//            val updatedEventMaps = updatedEventsOfDay.events.domainsToMaps()
            eventRemoteDataSource.updateEventsOfDay(DayMap(updatedEventsOfDay.domainToMap(groupId)))
        } else {
            val dayMap = DayMap(updatedEventsOfDay.domainToMap(groupId))
            eventRemoteDataSource.setDay(dayMap)
            dayLocalDataSource.upsert(dayMap.mapToEntity())
        }
    }

}