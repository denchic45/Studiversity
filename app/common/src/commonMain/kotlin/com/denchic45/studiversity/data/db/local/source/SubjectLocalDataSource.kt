package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.SubjectEntity
import com.denchic45.studiversity.SubjectEntityQueries
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubjectLocalDataSource @Inject constructor(db: AppDatabase) {
    private val queries: SubjectEntityQueries = db.subjectEntityQueries

    suspend fun upsert(subjectEntity: SubjectEntity) = withContext(Dispatchers.IO) {
        queries.upsert(subjectEntity)
    }

    suspend fun upsert(subjectEntities: List<SubjectEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            subjectEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(subjectId: String): SubjectEntity? = withContext(Dispatchers.IO) {
        queries.getById(subjectId).executeAsOneOrNull()
    }

    fun observe(id: String): Flow<SubjectEntity?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    fun observeByGroupId(groupId: String): Flow<List<SubjectEntity>> {
        return queries.getByGroupId(groupId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun delete(id:String) = withContext(Dispatchers.IO) {
        queries.delete(id)
    }
}