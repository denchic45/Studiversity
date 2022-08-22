package com.denchic45.kts.data.service

import com.denchic45.kts.data.network.model.SignInWithPasswordRequest
import com.denchic45.kts.data.network.model.SignInWithPasswordResponse
import com.denchic45.kts.data.pref.AppPreferences
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

actual class AuthService @Inject constructor(
    private val client: HttpClient,
    private val appPreferences: AppPreferences,
) {

    actual val isAuthenticated: Boolean
        get() = TODO("Not yet implemented")

    actual val observeIsAuthenticated: Flow<Boolean>
        get() = TODO("Not yet implemented")

    actual suspend fun signInWithEmailAndPassword(email: String, password: String) {
        client.post {
            url("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword")
            parameter("key", "AIzaSyB76HAiBD81LwU4_L9ocbE1IOERYm3RMt8")
            contentType(ContentType.Application.Json)
            setBody(SignInWithPasswordRequest(email, password))
        }.body<SignInWithPasswordResponse>().apply {
            appPreferences.token = idToken
            appPreferences.refreshToken = refreshToken
        }
    }

    actual suspend fun resetPassword(email: String) {
    }

    actual suspend fun createNewUser(email: String, password: String) {
    }
}