package com.denchic45.kts.di.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.ui.login.LoginComponent
import com.denchic45.kts.ui.root.RootComponent
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class JvmAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
) {
    abstract val rootComponent: RootComponent

    abstract val loginComponent: LoginComponent

    @Provides
    fun provideComponentContext(): ComponentContext {
        return DefaultComponentContext(LifecycleRegistry())
    }

    @Provides
    fun provideHttp(appPreferences: AppPreferences) = HttpClient {
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
        // TODO add second http client for firebase
//        install(Auth) {
//            bearer {
//                appPreferences.token?.let { token ->
//                    appPreferences.refreshToken?.let { refreshToken ->
//                        loadTokens {
//                            BearerTokens(token, refreshToken)
//                        }
//                    }
//                }
//                refreshTokens {
//                    val response: RefreshTokenResponse = client.submitForm(
//                        url = "https://securetoken.googleapis.com/v1/token",
//                        formParameters = Parameters.build {
//                            append("grant_type", "refresh_token")
//                            append("refresh_token", oldTokens?.refreshToken ?: "")
//                        }) {
//                        parameter("key", "AIzaSyB76HAiBD81LwU4_L9ocbE1IOERYm3RMt8")
//                        markAsRefreshTokenRequest()
//                    }.body()
//                    appPreferences.apply {
//                        token = response.id_token
//                        refreshToken = response.refresh_token
//                    }
//                    BearerTokens(response.id_token, response.refresh_token)
//                }
//            }
//        }
    }
}

