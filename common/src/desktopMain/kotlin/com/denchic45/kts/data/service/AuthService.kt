package com.denchic45.kts.data.service

import kotlinx.coroutines.flow.Flow

actual class AuthService {

    actual val isAuthenticated: Boolean
        get() = TODO("Not yet implemented")

    actual val observeIsAuthenticated: Flow<Boolean>
        get() = TODO("Not yet implemented")

    actual suspend fun signInWithEmailAndPassword(email: String, password: String) {
    }

    actual suspend fun resetPassword(email: String) {
    }

    actual suspend fun createNewUser(email: String, password: String) {
    }

}