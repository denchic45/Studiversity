package com.denchic45.kts.data.service

import kotlinx.coroutines.flow.Flow

expect class AuthService {

    val isAuthenticated: Boolean

    val observeIsAuthenticated: Flow<Boolean>

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun resetPassword(email: String)

    suspend fun createNewUser(email: String, password: String)
}