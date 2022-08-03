package com.denchic45.kts.data.repository

import android.util.Log
import com.denchic45.kts.AppDatabase
import com.denchic45.kts.DayEntity
import com.denchic45.kts.data.local.db.*
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.model.domain.GroupTimetable
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.remote.model.*
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.di.modules.IoDispatcher
import com.denchic45.kts.domain.model.EventsOfDay
import com.denchic45.kts.domain.model.GroupHeader
import com.denchic45.kts.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import javax.inject.Inject

class EventRepository @Inject constructor(
    override val networkService: NetworkService,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase,
    private val eventLocalDataSource: EventLocalDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val teacherEventLocalDataSource: TeacherEventLocalDataSource,
    private val groupPreferences: GroupPreferences,
    private val firestore: FirebaseFirestore,
    private val dayLocalDataSource: DayLocalDataSource,
    private val subjectLocalDataSource: SubjectLocalDataSource,
    private val userPreferences: UserPreferences,
    private val appPreferences: AppPreferences,
    override val appVersionService: AppVersionService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
) : Repository(), SaveGroupOperation {

    private val groupsRef = firestore.collection("Groups")
    private val daysRef: Query = firestore.collectionGroup("Days")

    fun findEventsOfDayByGroupIdAndDate(groupId: String, date: LocalDate): Flow<EventsOfDay> {
        addListenerRegistrationIfNotExist("$date of $groupId") {
            getQueryOfEventsOfGroupByDate(date, groupId)
                .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    coroutineScope.launch(dispatcher) {
                        snapshots?.let {
                            if (!snapshots.isEmpty) {
                                saveDay(DayMap(snapshots.documents[0].toMutableMap()))
                            }
                        }
                    }
                }
        }
        return eventLocalDataSource.observeEventsByDateAndGroupId(date, groupId)
            .map {
                it.entityToUserDomain()
            }
    }

    fun findEventsOfDayByYourGroupAndDate(date: LocalDate): Flow<EventsOfDay> {
        return groupPreferences.observeGroupId
            .filter(String::isNotEmpty)
            .flatMapLatest { groupId ->
                eventLocalDataSource.observeEventsByDateAndGroupId(date, groupId)
                    .run {
                        if (date.toDateUTC() > nextSaturday || date.toDateUTC() < previousMonday) {
                            withSnapshotListener(
                                query = getQueryOfEventsOfGroupByDate(
                                    date,
                                    groupId
                                ),
                                onQuerySnapshot = {
                                    if (!it.isEmpty) {
                                        coroutineScope.launch {
                                            saveDay(DayMap(it.documents[0].toMutableMap()))
                                        }
                                    }
                                }
                            )
                        } else this
                    }
                    .map {
                        it.entityToUserDomain()
                    }
                    .distinctUntilChanged()
            }
    }

    fun findEventsForDayForTeacherByDate(date: LocalDate): Flow<EventsOfDay> {
        return callbackFlow {
            val teacherId = userPreferences.id
            launch {
                eventLocalDataSource.observeEventsByDateAndTeacherId(date, teacherId)
                    .distinctUntilChanged()
                    .map { it.entitiesToEventsOfDay(date) }
                    .collect { send(it) }
            }
            val eventsOfTeacherByDate = eventsOfTeacherByDate(date, teacherId)
            awaitClose(eventsOfTeacherByDate::remove)
        }
    }

    private fun getQueryOfEventsOfGroupByDate(date: LocalDate, groupId: String): Query {
        val toDate = date.toDateUTC()
        return groupsRef
            .document(groupId)
            .collection("Days")
            .whereEqualTo("date", toDate)
    }

    private suspend fun saveDay(dayMap: DayMap) {
        coroutineScope.launch {
            val notRelatedTeacherEntities =
                courseLocalDataSource.getNotRelatedTeacherIdsToGroup(
                    dayMap.teacherIds,
                    dayMap.groupId
                ).map { teacherId ->
                    firestore.collection("Users")
                        .document(teacherId)
                        .get()
                        .await()
                        .toMap(::UserMap).mapToUserEntity()
                }

            val notRelatedSubjectEntities =
                courseLocalDataSource.getNotRelatedSubjectIdsToGroup(
                    dayMap.subjectIds,
                    dayMap.groupId
                ).map { subjectId ->
                    firestore.collection("Subjects").document(subjectId)
                        .get()
                        .await()
                        .toMap(::SubjectMap).mapToSubjectEntity()
                }

//                userLocalDataSource.upsert(notRelatedTeacherEntities)
//                subjectLocalDataSource.upsert(notRelatedSubjectEntities)

//                dayLocalDataSource.deleteByDate(dayDoc.date)
//                dayLocalDataSource.upsert(dayDoc.mapToEntity())

            val eventEntities = dayMap.events.docsToEntities(dayMap.id)
//                eventLocalDataSource.upsert(eventEntities)

//                teacherEventLocalDataSource.insert(eventEntities.toTeacherEventEntities())

            dayLocalDataSource.saveDay(
                notRelatedTeacherEntities = notRelatedTeacherEntities,
                notRelatedSubjectEntities = notRelatedSubjectEntities,
                dayEntity = dayMap.mapToEntity(),
                eventEntities = dayMap.events.docsToEntities(dayMap.id),
                teacherEventEntities = eventEntities.toTeacherEventEntities()
            )
        }
    }

    private fun eventsOfTeacherByDate(
        date: LocalDate,
        teacherId: String,
    ): ListenerRegistration {
        return firestore.collectionGroup("Days")
            .whereArrayContains("teacherIds", teacherId)
            .whereEqualTo("date", date.toDateUTC())
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.d("lol", "getLessonsOfTeacherByDateListener: ", error)
                    return@addSnapshotListener
                }
                if (!snapshot!!.isEmpty) {
                    val dayMaps = snapshot.toMutableMaps(::DayMap)
                    for (dayMap in dayMaps) {
                        coroutineScope.launch(dispatcher) {
                            if (!groupLocalDataSource.isExist(dayMap.groupId)) {
                                val documentSnapshot = groupsRef.document(dayMap.groupId)
                                    .get()
                                    .await()
                                if (documentSnapshot.exists())
                                    saveGroup(GroupMap(documentSnapshot.toMap()))
                            }
                            saveDay(dayMap)
                        }
                    }
                }
            }
    }

    var lessonTime: Int
        get() = appPreferences.lessonTime
        set(lessonTime) {
            appPreferences.lessonTime = lessonTime
        }

    fun observeEventsOfYourGroup() {
        if (!hasListener("lessonsOfYouGroup")) addListenerRegistrationIfNotExist("lessonsOfYouGroup") {
            daysRef.whereGreaterThanOrEqualTo("date", previousMonday)
                .whereLessThanOrEqualTo("date", nextSaturday)
                .whereEqualTo("groupId", groupPreferences.groupId)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Log.d("lol", "onEvent: ", error)
                        return@addSnapshotListener
                    }
                    if (!querySnapshot!!.isEmpty) {
                        coroutineScope.launch(dispatcher) {
                            for (dayDoc in querySnapshot.toMutableMaps(::DayMap)) {
                                saveDay(dayDoc)
                            }
                        }
                    }
                }
        }
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
        val batch = firestore.batch()
        groupTimetables.map { groupTimetable ->
            addGroupTimetable(batch, groupTimetable)
        }
        batch.commit().await()
    }

    private suspend fun addGroupTimetable(
        batch: WriteBatch,
        groupTimetable: GroupTimetable,
    ) {
        val groupWeekEvents = groupTimetable.weekEvents
        val dayRef = groupsRef.document(groupTimetable.groupHeader.id).collection("Days")
        eventLocalDataSource.deleteByGroupAndDateRange(
            groupTimetable.groupHeader.id,
            groupWeekEvents[0].date,
            groupWeekEvents[5].date
        )

        val existsDayDocs: List<DayMap> = getQueryOfWeekDays(groupTimetable, dayRef)
            .await()
            .toMutableMaps(::DayMap)

        for (eventsOfTheDay in groupWeekEvents) {
            val maybeDayMap = findDayByDate(existsDayDocs, eventsOfTheDay.date.toDateUTC())

            val addableEvents = eventsOfTheDay.events.map { EventMap(it.domainToMap()) }

            val dayMap: DayMap = maybeDayMap?.let {
                it.events = addableEvents
                it
            } ?: DayMap(eventsOfTheDay.domainToMap(groupTimetable.groupHeader.id))

            batch[dayRef.document(dayMap.id), dayMap] = SetOptions.merge()

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

    private fun getQueryOfWeekDays(
        groupTimetable: GroupTimetable,
        daysRef: CollectionReference,
    ): Task<QuerySnapshot> {
        val monday = groupTimetable.weekEvents[0].date.toDateUTC()
        val saturday = groupTimetable.weekEvents[5].date.toDateUTC()
        return daysRef.whereGreaterThanOrEqualTo("date", monday)
            .whereLessThanOrEqualTo("date", saturday)
            .get()
    }


    suspend fun updateEventsOfDay(eventsOfDay: EventsOfDay, groupHeader: GroupHeader) {
        val dayDocId = dayLocalDataSource.getIdByDateAndGroupId(eventsOfDay.date, groupHeader.id)
        if (isNetworkNotAvailable) return
        val daysRef = groupsRef.document(groupHeader.id)
            .collection("Days")
        if (dayDocId != null) {
            val snapshot = daysRef.whereEqualTo("date", eventsOfDay.date.toDateUTC())
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val dayMap = DayMap(snapshot.documents[0].toMutableMap())
                dayLocalDataSource.upsert(dayMap.mapToEntity())

                val eventDocs = eventsOfDay.events.domainsToMaps()
                daysRef.document(dayMap.id)
                    .update(
                        "events",
                        eventDocs,
                        "timestamp",
                        FieldValue.serverTimestamp()
                    ).await()
            }
        } else {
            val dayMap = DayMap(eventsOfDay.domainToMap(groupHeader.id))
            daysRef.document(dayMap.id).set(dayMap, SetOptions.merge()).await()
            dayLocalDataSource.upsert(dayMap.mapToEntity())
        }
    }

}