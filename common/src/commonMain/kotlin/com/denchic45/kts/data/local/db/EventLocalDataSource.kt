package com.denchic45.kts.data.local.db

import com.denchic45.kts.*
import com.denchic45.kts.data.local.model.DayWithEventsEntities
import com.denchic45.kts.util.DatePatterns
import com.denchic45.kts.util.toString
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate

class EventLocalDataSource(db: AppDatabase) {

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

    fun observeEventsByDateAndGroupId(
        date: LocalDate, groupId: String
    ): Flow<DayWithEventsEntities> {
        return getDayByDateAndGroupId(date, groupId)
            .flatMapLatest { dayEntity ->
                dayEntity?.let {
                    observeEventsByDayId(dayEntity.day_id).map { events ->
                        DayWithEventsEntities(dayEntity, events)
                    }
                } ?: flowOf(
                    DayWithEventsEntities(
                        DayEntity(
                            day_id = "",
                            date = date.toString(DatePatterns.yyy_MM_dd),
                            start_at_zero = false,
                            group_id = groupId
                        ),
                        emptyList()
                    )
                )
            }
    }

    fun getDayByDateAndGroupId(
        date: LocalDate,
        groupId: String
    ): Flow<DayEntity?> {
        return queries.getDayByDateAndGroupId(date.toString(DatePatterns.yyy_MM_dd), groupId)
            .asFlow().mapToOneOrNull()
    }

    fun observeEventsByDayId(dayId: String): Flow<List<EventWithSubjectAndGroupAndTeachers>> {
        return queries.getEventsWithSubjectAndTeachersByDayId(dayId).asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun observeEventsByDateAndTeacherId(
        date: LocalDate,
        teacherId: String
    ): Flow<List<EventWithSubjectAndGroupAndTeachers>> {
        return queries.getEventsWithSubjectAndTeachersByDateAndTeacherId(
            date.toString(DatePatterns.yyy_MM_dd),
            teacherId
        ).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun deleteByGroupAndDateRange(
        groupId: String,
        start: LocalDate,
        end: LocalDate
    ) = withContext(Dispatchers.IO) {
        queries.deleteByGroupAndDateRange(
            start.toString(DatePatterns.yyy_MM_dd),
            end.toString(DatePatterns.yyy_MM_dd),
            groupId
        )
    }
}