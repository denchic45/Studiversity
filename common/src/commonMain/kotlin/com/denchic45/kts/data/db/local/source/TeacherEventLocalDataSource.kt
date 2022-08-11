package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.TeacherEventEntity
import com.denchic45.kts.TeacherEventEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeacherEventLocalDataSource @Inject constructor(db: AppDatabase) {
    private val queries: TeacherEventEntityQueries = db.teacherEventEntityQueries

    suspend fun insert(teacherEventEntity: TeacherEventEntity) = withContext(Dispatchers.IO) {
        queries.upsert(teacherEventEntity)
    }

    suspend fun insert(teacherEventEntities: List<TeacherEventEntity>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                teacherEventEntities.map {
                    queries.upsert(it)
                }
            }
        }
}