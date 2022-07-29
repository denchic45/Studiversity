package com.denchic45.kts.data.remote.db

import com.denchic45.kts.data.remote.model.UserMap
import kotlinx.coroutines.flow.Flow

expect class UserRemoteDataSource {

    fun observeById(id: String): Flow<UserMap?>

    fun findByContainsName(text: String): Flow<List<UserMap>>

    suspend fun findAndByEmail(email: String): UserMap
}