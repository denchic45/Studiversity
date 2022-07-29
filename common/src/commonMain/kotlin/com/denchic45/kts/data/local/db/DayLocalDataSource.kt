package com.denchic45.kts.data.local.db

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.DayEntity
import com.denchic45.kts.DayEntityQueries
import com.denchic45.kts.util.DatePatterns
import com.denchic45.kts.util.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayLocalDataSource(db: AppDatabase) {
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
        date: LocalDate, groupId: String
    ): String? = withContext(Dispatchers.IO) {
        queries.getIdByDateAndGroupId(date.toString(DatePatterns.yyy_MM_dd), groupId)
            .executeAsOneOrNull()
    }

    suspend fun deleteByDate(date: LocalDate) = withContext(Dispatchers.IO) {
        queries.deleteByDate(date.toString(DatePatterns.yyy_MM_dd))
    }
}