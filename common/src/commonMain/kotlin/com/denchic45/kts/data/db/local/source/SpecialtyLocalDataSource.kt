package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.SpecialtyEntity
import com.denchic45.kts.SpecialtyEntityQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SpecialtyLocalDataSource @Inject constructor(db: AppDatabase) {

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