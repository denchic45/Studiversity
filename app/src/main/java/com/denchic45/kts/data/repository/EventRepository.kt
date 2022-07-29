package com.denchic45.kts.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.denchic45.kts.AppDatabase
import com.denchic45.kts.data.database.DataBase
import com.denchic45.kts.data.local.db.*
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.model.domain.GroupTimetable
import com.denchic45.kts.data.model.mapper.*
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.prefs.AppPreference
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
    val userMapper: UserMapper,
    val specialtyMapper: SpecialtyMapper,
    private val dayMapper: DayMapper,
    override val dataBase: DataBase,
    private val appDatabase: AppDatabase,
    private val eventLocalDataSource: EventLocalDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val teacherEventLocalDataSource: TeacherEventLocalDataSource,
    private val groupPreferences: GroupPreferences,
    private val firestore: FirebaseFirestore,
    private val eventMapper: EventMapper,
    private val dayLocalDataSource: DayLocalDataSource,
    private val subjectLocalDataSource: SubjectLocalDataSource,
    private val userPreferences: UserPreferences,
    private val appPreference: AppPreference,
    override val appVersionService: AppVersionService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource
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
                                saveDay(DayMap(snapshots.documents[0].data!!))
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
                            Log.d("lol", "ON available date ")
                            Log.d("lol", "ON events coroutine launch: ")
                            withSnapshotListener(
                                query = getQueryOfEventsOfGroupByDate(
                                    date,
                                    groupId
                                ),
                                onQuerySnapshot = {
                                    if (!it.isEmpty) {
                                        coroutineScope.launch {
                                            saveDay(DayMap(it.documents[0].data!!))
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
            Log.d("lol", "ON callback flow: ")
            val teacherId = userPreferences.id

            launch {
                eventLocalDataSource.observeEventsByDateAndTeacherId(date, teacherId)
                    .distinctUntilChanged()
                    .map { it.entitiesToEventsOfDay(date) }
                    .collect {
                        send(it)
                    }
            }


            val eventsOfTeacherByDate = eventsOfTeacherByDate(date, teacherId)
            awaitClose {
                Log.d("lol", "ON awaitClose: ")
                eventsOfTeacherByDate.remove()
            }


        }
    }

    private fun getQueryOfEventsOfGroupByDate(date: LocalDate, groupId: String): Query {
        val toDate = date.toDateUTC()
        return groupsRef
            .document(groupId)
            .collection("Days")
            .whereEqualTo("date", toDate)
    }

    private suspend fun saveDay(dayDoc: DayMap) {
        appDatabase.transaction {
            coroutineScope.launch {
                courseLocalDataSource.getNotRelatedTeacherIdsToGroup(
                    dayDoc.teacherIds,
                    dayDoc.groupId
                )
                    .map { teacherId ->
                        firestore.collection("Users")
                            .document(teacherId)
                            .get()
                            .await()
                            .apply {
                                userLocalDataSource.upsert(UserMap(data!!).mapToUserEntity())
                            }
                    }

                courseLocalDataSource.getNotRelatedSubjectIdsToGroup(
                    dayDoc.subjectIds,
                    dayDoc.groupId
                )
                    .map { subjectId: String ->
                        firestore.collection("Subjects").document(subjectId)
                            .get()
                            .await()
                            .apply {
                                subjectLocalDataSource.upsert(
                                    SubjectMap(data!!).mapToSubjectEntity()
                                )
                            }
                    }

                dayLocalDataSource.deleteByDate(dayDoc.date.toLocalDate())
                dayLocalDataSource.upsert(dayDoc.mapToEntity())

                val eventEntities = dayDoc.events.docsToEntities(dayDoc.id)
                eventLocalDataSource.upsert(eventEntities)

                teacherEventLocalDataSource.insert(eventEntities.toTeacherEventEntities())
            }
        }
    }

    private fun eventsOfTeacherByDate(
        date: LocalDate,
        teacherId: String
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
                    val dayDocs = snapshot.documents.map { DayMap(it.data!!) }
                    for (dayDoc in dayDocs) {
                        coroutineScope.launch(dispatcher) {
                            dataBase.withTransaction {
                                if (!groupLocalDataSource.isExist(dayDoc.groupId)) {
                                    val documentSnapshot = groupsRef.document(dayDoc.groupId)
                                        .get()
                                        .await()
                                    if (documentSnapshot.exists())
                                        saveGroup(GroupMap(documentSnapshot.data!!))
                                }
                                saveDay(dayDoc)
                            }
                        }
                    }
                }
            }
    }

    var lessonTime: Int
        get() = appPreference.lessonTime
        set(lessonTime) {
            appPreference.lessonTime = lessonTime
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
                            for (dayDoc in querySnapshot.documents.map { DayMap(it.data!!) }) {
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
        groupTimetables.map { groupTimetable: GroupTimetable ->
            addGroupTimetable(batch, groupTimetable)
        }
        batch.commit().await()
    }

    private suspend fun addGroupTimetable(
        batch: WriteBatch,
        groupTimetable: GroupTimetable
    ) {
        val groupWeekEvents = groupTimetable.weekEvents
        val dayRef = groupsRef.document(groupTimetable.groupHeader.id).collection("Days")
        eventLocalDataSource.deleteByGroupAndDateRange(
            groupTimetable.groupHeader.id,
            groupWeekEvents[0].date,
            groupWeekEvents[5].date
        )

        val existsDayDocs: List<DayDoc> = getQueryOfWeekDays(groupTimetable, dayRef)
            .await()
            .toObjects(DayDoc::class.java)

        for (eventsOfTheDay in groupWeekEvents) {
            val maybeDayDoc = findDayByDate(existsDayDocs, eventsOfTheDay.date.toDateUTC())

            val addableEvents = eventMapper.domainToDoc(eventsOfTheDay.events)

            val dayDoc: DayDoc = maybeDayDoc?.let {
                it.events = addableEvents
                it
            } ?: dayMapper.domainToDoc(eventsOfTheDay, groupTimetable.groupHeader.id)

            batch[dayRef.document(dayDoc.id), dayDoc] = SetOptions.merge()

            dayLocalDataSource.upsert(
                com.denchic45.kts.DayEntity(
                    day_id = dayDoc.id,
                    date = dayDoc.date.toString(DatePatterns.yyy_MM_dd),
                    start_at_zero = eventsOfTheDay.startsAtZero,
                    group_id = groupTimetable.groupHeader.id
                )
            )
        }

    }

    private fun findDayByDate(dayDocs: List<DayDoc>, date: Date): DayDoc? {
        return dayDocs.firstOrNull { dayDoc: DayDoc -> dayDoc.date == date }
    }

    private fun getQueryOfWeekDays(
        groupTimetable: GroupTimetable,
        daysRef: CollectionReference
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
                val dayMap = DayMap(snapshot.documents[0].data!!)
                dayLocalDataSource.upsert(dayMap.mapToEntity())

                val eventDocs = eventMapper.domainToDoc(eventsOfDay.events)
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