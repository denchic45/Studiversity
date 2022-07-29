package com.denchic45.kts.data.remote.db

import com.denchic45.kts.data.remote.model.UserMap
import kotlinx.coroutines.flow.Flow

actual class UserRemoteDataSource {
    actual fun observeById(id: String): Flow<UserMap?> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<UserMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findAndByEmail(email: String): UserMap {
        TODO("Not yet implemented")
    }

}