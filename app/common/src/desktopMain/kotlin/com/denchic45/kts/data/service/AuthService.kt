//package com.denchic45.kts.data.service
//
//import com.denchic45.kts.ApiKeys
//import com.denchic45.kts.data.service.model.SignInWithPasswordRequest
//import com.denchic45.kts.data.service.model.SignInWithPasswordResponse
//import com.denchic45.kts.data.pref.AppPreferences
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import javax.inject.Inject
//
//@me.tatarka.inject.annotations.Inject
//actual class AuthService @Inject constructor(
//    private val client: HttpClient,
//    private val appPreferences: AppPreferences,
//) {
//
//    actual val isAuthenticated: Boolean
//        get() = TODO("Not yet implemented")
//
//    actual val observeIsAuthenticated: Flow<Boolean> =
//        appPreferences.observeToken.map { !it.isNullOrEmpty() }
//
//    actual suspend fun signInWithEmailAndPassword(email: String, password: String) {
//        println("AUTH: try... desktop")
//        client.post {
//            url("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword")
//            parameter("key", ApiKeys.firebaseApiKey)
//            contentType(ContentType.Application.Json)
//            setBody(SignInWithPasswordRequest(email, password))
//        }
//            .body<SignInWithPasswordResponse>().apply {
//                appPreferences.token = idToken
//                appPreferences.refreshToken = refreshToken
//            }
//    }
//
//    actual suspend fun resetPassword(email: String) {
//    }
//
//    actual suspend fun createNewUser(email: String, password: String) {
//    }
//
//    actual fun signOut() {
//    }
//}