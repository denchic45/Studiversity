package com.denchic45.kts.di

import com.denchic45.kts.ApiKeys
import com.denchic45.kts.data.network.model.RefreshTokenResponse
import com.denchic45.kts.data.pref.AppPreferences
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class NetworkComponent {

    @Provides
    fun provideHttpClient() = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    @Provides
    fun provideFirebaseHttpClient(
        httpClient: HttpClient,
        appPreferences: AppPreferences,
    ): FirebaseHttpClient = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {

            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(Auth) {
            bearer {
                appPreferences.token?.let { token ->
                    appPreferences.refreshToken?.let { refreshToken ->
                        loadTokens {
                            BearerTokens(token, refreshToken)
                        }
                    }
                }
                refreshTokens {
                    val response: RefreshTokenResponse =
                        httpClient.submitForm(
                            url = "https://securetoken.googleapis.com/v1/token",
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                            }) {
                            parameter("key", ApiKeys.firebaseApiKey)
                            markAsRefreshTokenRequest()
                        }.body()

                    appPreferences.apply {
                        token = response.id_token
                        refreshToken = response.refresh_token
                    }
                    BearerTokens(response.id_token, response.refresh_token)
                }
            }
        }
    }
}

typealias FirebaseHttpClient = HttpClient