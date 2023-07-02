package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.UserEntity
import com.denchic45.studiversity.UserEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class UserLocalDataSource(db: AppDatabase) {

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

    fun updateAvatar(userId: String, avatarUrl: String, isGenerated: Boolean) {
        queries.updateAvatar(avatarUrl, isGenerated, userId)
    }
}