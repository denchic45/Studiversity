package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.*
import com.denchic45.kts.data.db.local.model.DayWithEventsEntities
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class EventLocalDataSource @Inject constructor(db: AppDatabase) {

    private val queries: EventEntityQueries = db.eventEntityQueries


    suspend fun upsert(eventEntity: EventEntity) = withContext(Dispatchers.IO) {
        queries.upsert(eventEntity)
    }

    suspend fun upsert(eventEntities: List<EventEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            eventEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): EventEntity? = withContext(Dispatchers.IO) {
        queries.getById(id).executeAsOneOrNull()
    }

    fun observeDayEventsByDateAndGroupId(
        date: LocalDate,
        groupId: String,
    ): Flow<DayWithEventsEntities?> {
        return observeDayByDateAndGroupId(date, groupId)
            .flatMapLatest { dayEntity ->
                dayEntity?.let {
                    observeEventsByDayId(dayEntity.day_id).map { events ->
                        DayWithEventsEntities(dayEntity, events)
                    }
                } ?: flowOf(null)
            }
    }

    private fun observeDayByDateAndGroupId(
        date: LocalDate,
        groupId: String,
    ): Flow<DayEntity?> {
        return queries.getDayByDateAndGroupId(date.toString(DateTimePatterns.yyy_MM_dd), groupId)
            .asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    private fun observeEventsByDayId(dayId: String): Flow<List<EventWithSubjectAndGroupAndTeachers>> {
        return queries.getEventsWithSubjectAndTeachersByDayId(dayId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun observeEventsByDateAndTeacherId(
        date: LocalDate,
        teacherId: String,
    ): Flow<List<EventWithSubjectAndGroupAndTeachers>> {
        return queries.getEventsWithSubjectAndTeachersByDateAndTeacherId(
            date.toString(DateTimePatterns.yyy_MM_dd),
            teacherId
        ).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun deleteByGroupAndDateRange(
        groupId: String,
        start: LocalDate,
        end: LocalDate,
    ) = withContext(Dispatchers.IO) {
        queries.deleteByGroupAndDateRange(
            start.toString(DateTimePatterns.yyy_MM_dd),
            end.toString(DateTimePatterns.yyy_MM_dd),
            groupId
        )
    }

    fun observeEventsByDateRangeAndGroupId(
        groupId: String,
        dates: List<LocalDate>,
    ): Flow<List<DayWithEventsEntities?>> {
        return combine(dates.map { observeDayEventsByDateAndGroupId(it, groupId) }) { it.toList() }
    }
}