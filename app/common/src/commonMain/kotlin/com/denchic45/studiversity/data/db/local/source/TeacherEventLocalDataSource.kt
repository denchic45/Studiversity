package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.EventMember
import com.denchic45.studiversity.entity.EventMemberQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class TeacherEventLocalDataSource(db: AppDatabase) {
    private val queries: EventMemberQueries = db.eventMemberQueries

    suspend fun insert(eventMember: EventMember) = withContext(Dispatchers.IO) {
        queries.upsert(eventMember)
    }

    suspend fun insert(teacherEventEntities: List<EventMember>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                teacherEventEntities.map {
                    queries.upsert(it)
                }
            }
        }
}