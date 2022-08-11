package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.*
import com.denchic45.kts.util.DatePatterns
import com.denchic45.kts.util.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DayLocalDataSource @Inject constructor(private val db: AppDatabase) {
    private val queries: DayEntityQueries = db.dayEntityQueries

    suspend fun upsert(dayEntity: DayEntity) = withContext(Dispatchers.IO) {
        queries.upsert(dayEntity)
    }

    suspend fun upsert(dayEntities: List<DayEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            dayEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): DayEntity = withContext(Dispatchers.IO) {
        queries.getById(id).executeAsOne()
    }

    suspend fun getIdByDateAndGroupId(
        date: LocalDate, groupId: String,
    ): String? = withContext(Dispatchers.IO) {
        queries.getIdByDateAndGroupId(date.toString(DatePatterns.yyy_MM_dd), groupId)
            .executeAsOneOrNull()
    }


    fun saveDay(
        notRelatedTeacherEntities: List<UserEntity>,
        notRelatedSubjectEntities: List<SubjectEntity>,
        dayEntity: DayEntity,
        eventEntities: List<EventEntity>,
        teacherEventEntities: List<TeacherEventEntity>,
    ) {
        db.transaction {
            notRelatedTeacherEntities.forEach { db.userEntityQueries.upsert(it) }
            notRelatedSubjectEntities.forEach { db.subjectEntityQueries.upsert(it) }
            queries.apply {
                deleteById(dayEntity.day_id)
                upsert(dayEntity)
            }
            db.eventEntityQueries.apply {
                getEventIdsByDayId(dayEntity.day_id).executeAsList().let { eventIds ->
                    eventIds.forEach { eventId ->
                        deleteByEventId(eventId)
                        db.teacherEventEntityQueries.deleteByEventId(eventId)
                    }
                }
                eventEntities.forEach { upsert(it) }
            }
            teacherEventEntities.forEach { db.teacherEventEntityQueries.upsert(it) }
        }
    }

    fun deleteById(dayId: String) {
        queries.deleteById(dayId)
    }
}