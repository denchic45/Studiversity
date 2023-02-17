package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.*
import com.denchic45.stuiversity.util.DatePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class DayLocalDataSource @Inject constructor(private val db: AppDatabase) {
    private val dayEntityQueries: DayEntityQueries = db.dayEntityQueries

    suspend fun upsert(dayEntity: DayEntity) = withContext(Dispatchers.IO) {
        dayEntityQueries.upsert(dayEntity)
    }

    suspend fun upsert(dayEntities: List<DayEntity>) = withContext(Dispatchers.IO) {
        dayEntityQueries.transaction {
            dayEntities.forEach { dayEntityQueries.upsert(it) }
        }
    }

    suspend fun get(id: String): DayEntity = withContext(Dispatchers.IO) {
        dayEntityQueries.getById(id).executeAsOne()
    }

    suspend fun getIdByDateAndGroupId(
        date: LocalDate, groupId: String,
    ): String? = withContext(Dispatchers.IO) {
        dayEntityQueries.getIdByDateAndGroupId(date.toString(DatePatterns.yyy_MM_dd), groupId)
            .executeAsOneOrNull()
    }


    suspend fun saveDay(
        notRelatedTeacherEntities: List<UserEntity>,
        notRelatedSubjectEntities: List<SubjectEntity>,
        dayEntity: DayEntity,
        eventEntities: List<EventEntity>,
        teacherEventEntities: List<TeacherEventEntity>,
    ) {
//        withContext(Dispatchers.IO) {
        println("object db: $db")
            db.transaction {
                notRelatedTeacherEntities.forEach { db.userEntityQueries.upsert(it) }
                notRelatedSubjectEntities.forEach { db.subjectEntityQueries.upsert(it) }
                dayEntityQueries.apply {
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
//        }
    }

    fun deleteById(dayId: String) {
        dayEntityQueries.deleteById(dayId)
    }
}