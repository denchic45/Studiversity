package com.denchic45.kts.data.service

import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.di.GuestHttpClient
import com.denchic45.kts.domain.EmptyResource
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.toEmptyResource
import com.denchic45.kts.domain.toResource
import com.denchic45.stuiversity.api.auth.AuthApi
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignInResponse
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.onSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AuthService @javax.inject.Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val appPreferences: AppPreferences,
    private val userPreferences: UserPreferences,
    private val guestHttpClient: GuestHttpClient
) {
    val isAuthenticated: Boolean
        get() = appPreferences.token.isNotEmpty()

    val observeIsAuthenticated: Flow<Boolean>
        get() = appPreferences.observeToken.map(String::isNotEmpty)

    val observeCurrentUser: Flow<Resource<UserResponse>> = userPreferences.observeId
        .filter(String::isNotEmpty)
        .map { userApi.getById(UUID.fromString(it)) }
        .onEach { it.onSuccess(::saveUserPreference) }
        .map { value -> value.toResource() }

    suspend fun signInByEmailPassword(
        email: String,
        password: String,
    ): EmptyResource {
        return authApi.signInByEmailPassword(SignInByEmailPasswordRequest(email, password))
            .onSuccess(::saveTokens)
            .flatMap { userApi.getMe() }
            .onSuccess(::saveUserPreference)
            .toEmptyResource()
    }

    private fun saveTokens(response: SignInResponse) {
        appPreferences.apply {
            token = response.token
            refreshToken = response.refreshToken
            organizationId = response.organizationId.toString()
            println("TOKENS: $token $refreshToken")
        }
    }

    private fun saveUserPreference(userResponse: UserResponse) {
        println("A save user data")
        userPreferences.apply {
            id = userResponse.id.toString()
            firstName = userResponse.firstName
            surname = userResponse.surname
            patronymic = userResponse.patronymic ?: ""
            gender = userResponse.gender.name
            avatarUrl = userResponse.avatarUrl
            isGeneratedAvatar = userResponse.generatedAvatar
            email = userResponse.account.email
        }
    }

    suspend fun checkDomain(url:String): HttpResponse {
      return guestHttpClient.get(url) {
            url {
                appendPathSegments("ping")
            }
        }
    }

    suspend fun resetPassword(email: String) {}

    fun signOut() {
        // TODO: Remove tokens and other data
    }
}