package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.UserEntity
import com.denchic45.kts.UserEntityQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UserLocalDataSource @Inject constructor(db: AppDatabase) {

    private val queries: UserEntityQueries = db.userEntityQueries

    suspend fun upsert(userEntity: UserEntity) = withContext(Dispatchers.Default) {
        queries.upsert(userEntity)
    }

    suspend fun upsert(userEntities: List<UserEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            userEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): UserEntity? = withContext(Dispatchers.Default) {
        queries.getById(id).executeAsOneOrNull()
    }

    suspend fun getAll(): List<UserEntity> = withContext(Dispatchers.Default) {
        queries.getAll().executeAsList()
    }

    fun observe(id: String): Flow<UserEntity?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    suspend fun isExist(id: String) = withContext(Dispatchers.IO) {
        queries.isExist(id).executeAsOne()
    }
}