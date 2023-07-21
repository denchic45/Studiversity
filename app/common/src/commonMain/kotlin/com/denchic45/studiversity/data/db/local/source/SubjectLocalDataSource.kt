package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Subject
import com.denchic45.studiversity.entity.SubjectQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SubjectLocalDataSource(db: AppDatabase) {
    private val queries: SubjectQueries = db.subjectQueries

    suspend fun upsert(Subject: Subject) = withContext(Dispatchers.IO) {
        queries.upsert(Subject)
    }

    suspend fun upsert(subjectEntities: List<Subject>) = withContext(Dispatchers.IO) {
        queries.transaction {
            subjectEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(subjectId: String): Subject? = withContext(Dispatchers.IO) {
        queries.getById(subjectId).executeAsOneOrNull()
    }

    fun observe(id: String): Flow<Subject?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    suspend fun delete(id:String) = withContext(Dispatchers.IO) {
        queries.delete(id)
    }
}