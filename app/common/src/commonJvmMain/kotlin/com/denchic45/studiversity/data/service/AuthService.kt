package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.db.local.DbHelper
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.di.GuestHttpClient
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.toResource
import com.denchic45.studiversity.util.SystemDirs
import com.denchic45.studiversity.util.databasePath
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignInResponse
import com.denchic45.stuiversity.api.common.toResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import okio.FileSystem

@Inject
class AuthService(
    private val coroutineScope: CoroutineScope,
    private val dbHelper: DbHelper,
    private val systemDirs: SystemDirs,
//    authApiLazy: Lazy<AuthApi>,
    private val appPreferences: AppPreferences,
    private val guestHttpClient: GuestHttpClient,
    private val client: HttpClient
) {

//    private val authApi by authApiLazy
//    val isAuthenticated: Boolean
//        get() = appPreferences.token.isNotEmpty()

    val observeIsAuthenticated: Flow<Boolean>
        get() = appPreferences.observeToken
            .map(String::isNotEmpty)
            .onEach { isAuth ->
                if (!isAuth)
                    clearAllDataIfExist()
            }

    private fun clearAllDataIfExist() {
        dbHelper.driver.close()
        val fileSystem = FileSystem.SYSTEM
        if (fileSystem.exists(systemDirs.databasePath))
            systemDirs.databasePath.toFile().delete()

        if (fileSystem.exists(systemDirs.prefsDir))
            fileSystem.list(systemDirs.prefsDir).forEach { fileSystem.delete(it) }

        val provider = client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().first()
        provider.clearToken()
    }

    suspend fun signInByEmailPassword(
        email: String,
        password: String,
    ): Resource<SignInResponse> {
        return guestHttpClient.post(appPreferences.url) {
            url {
                appendPathSegments("auth", "token")
            }
            contentType(ContentType.Application.Json)
            setBody(SignInByEmailPasswordRequest(email, password))
            parameter("grant_type", "password")
        }.toResult<SignInResponse>()
            .toResource()
            .onSuccess {
                coroutineScope.launch {
                    delay(1000)
                    saveTokens(it)
                }
            }
    }

    private fun saveTokens(response: SignInResponse) {
        appPreferences.apply {
            token = response.token
            refreshToken = response.refreshToken
            organizationId = response.organizationId.toString()
            println("SAVED TOKENS: $token $refreshToken")
        }
    }


    suspend fun checkDomain(url: String): HttpResponse {
        return guestHttpClient.get(url) {
            url {
                appendPathSegments("ping")
            }
        }
    }

    suspend fun resetPassword(email: String) {}

    fun signOut() {
        appPreferences.apply {
            token = ""
            refreshToken = ""
        }
    }
}