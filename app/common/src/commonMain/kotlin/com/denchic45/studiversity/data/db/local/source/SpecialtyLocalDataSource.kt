package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.SpecialtyEntity
import com.denchic45.studiversity.SpecialtyEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SpecialtyLocalDataSource(db: AppDatabase) {

    private val queries: SpecialtyEntityQueries = db.specialtyEntityQueries

    suspend fun upsert(specialtyEntity: SpecialtyEntity) = withContext(Dispatchers.IO) {
        queries.upsert(specialtyEntity)
    }

    suspend fun upsert(specialtyEntities: List<SpecialtyEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            specialtyEntities.forEach {
                queries.upsert(it)
            }
        }
    }

    fun observe(id: String): Flow<SpecialtyEntity?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }
}