package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.User
import com.denchic45.studiversity.entity.UserQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class UserLocalDataSource(db: AppDatabase) {
    private val queries: UserQueries = db.userQueries

    suspend fun upsert(User: User) = withContext(Dispatchers.Default) {
        queries.upsert(User)
    }

    suspend fun upsert(userEntities: List<User>) = withContext(Dispatchers.IO) {
        queries.transaction {
            userEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): User? = withContext(Dispatchers.Default) {
        queries.getById(id).executeAsOneOrNull()
    }

    suspend fun getAll(): List<User> = withContext(Dispatchers.Default) {
        queries.getAll().executeAsList()
    }

    fun observe(id: String): Flow<User?> {
        return queries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }

    suspend fun isExist(id: String) = withContext(Dispatchers.IO) {
        queries.isExist(id).executeAsOne()
    }

    fun updateAvatar(userId: String, avatarUrl: String, isGenerated: Boolean) {
        queries.updateAvatar(avatarUrl, isGenerated, userId)
    }
}