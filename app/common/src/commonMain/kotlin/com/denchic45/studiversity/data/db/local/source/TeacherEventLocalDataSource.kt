package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.TeacherEventEntity
import com.denchic45.studiversity.TeacherEventEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
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