package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.DayEntity
import com.denchic45.studiversity.entity.DayEntityQueries
import com.denchic45.studiversity.entity.EventEntity
import com.denchic45.studiversity.entity.EventMember
import com.denchic45.studiversity.entity.Subject
import com.denchic45.studiversity.entity.User
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
        notRelatedTeacherEntities: List<User>,
        notRelatedSubjectEntities: List<Subject>,
        dayEntity: DayEntity,
        eventEntities: List<EventEntity>,
        eventMembers: List<EventMember>,
    ) {
//        withContext(Dispatchers.IO) {
        println("object db: $db")
        db.transaction {
            notRelatedTeacherEntities.forEach { db.userQueries.upsert(it) }
            notRelatedSubjectEntities.forEach { db.subjectQueries.upsert(it) }
            dayEntityQueries.apply {
                deleteById(dayEntity.day_id)
                upsert(dayEntity)
            }
            db.eventEntityQueries.apply {
                getEventIdsByDayId(dayEntity.day_id).executeAsList().let { eventIds ->
                    eventIds.forEach { eventId ->
                        deleteByEventId(eventId)
                        db.eventMemberQueries.deleteByEventId(eventId)
                    }
                }
                eventEntities.forEach { upsert(it) }
            }
            eventMembers.forEach { db.eventMemberQueries.upsert(it) }
        }
//        }
    }

    fun deleteById(dayId: String) {
        dayEntityQueries.deleteById(dayId)
    }
}