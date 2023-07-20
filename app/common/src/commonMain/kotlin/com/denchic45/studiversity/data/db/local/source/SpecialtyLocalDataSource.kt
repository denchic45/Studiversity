package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Specialty
import com.denchic45.studiversity.entity.SpecialtyQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class SpecialtyLocalDataSource(db: AppDatabase) {

    private val queries: SpecialtyQueries = db.specialtyQueries

    suspend fun upsert(Specialty: Specialty) = withContext(Dispatchers.IO) {
        queries.upsert(Specialty)
    }

    suspend fun upsert(specialtyEntities: List<Specialty>) = withContext(Dispatchers.IO) {
        queries.transaction {
            specialtyEntities.forEach {
                queries.upsert(it)
            }
        }
    }

    fun observe(id: String): Flow<Specialty?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }
}