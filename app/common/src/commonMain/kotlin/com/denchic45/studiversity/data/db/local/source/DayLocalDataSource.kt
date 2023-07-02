package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.DayEntity
import com.denchic45.studiversity.DayEntityQueries
import com.denchic45.studiversity.EventEntity
import com.denchic45.studiversity.SubjectEntity
import com.denchic45.studiversity.TeacherEventEntity
import com.denchic45.studiversity.UserEntity
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class DayLocalDataSource(private val db: AppDatabase) {
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
        dayEntityQueries.getIdByDateAndGroupId(date.toString(DateTimePatterns.yyy_MM_dd), groupId)
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