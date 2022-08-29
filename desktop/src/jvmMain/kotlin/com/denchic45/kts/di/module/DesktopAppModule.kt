//package com.denchic45.kts.di.module
//
//import com.arkivanov.decompose.ComponentContext
//import com.denchic45.kts.data.network.model.RefreshTokenResponse
//import com.denchic45.kts.data.pref.AppPreferences
//import com.denchic45.kts.data.service.AppVersionService
//import com.denchic45.kts.data.service.FakeAppVersionService
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.auth.*
//import io.ktor.client.plugins.auth.providers.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.serialization.json.Json
//import javax.inject.Singleton
//
//@Module(
//    includes = [DesktopAppBindModule::class]
//)
//class DesktopAppModule(private val componentContext: ComponentContext) {
//
//    @Provides
//    @Singleton
//    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
//
//    @Provides
//    fun provideComponentContext() = componentContext
//
//    @Provides
//    @Singleton
//    fun provideHttpClient(appPreferences: AppPreferences) = HttpClient {
//        install(Logging) {
//            level = LogLevel.ALL
//        }
//        install(ContentNegotiation) {
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//            })
//        }
//        install(Auth) {
//            bearer {
//                loadTokens {
//                    BearerTokens(appPreferences.token, appPreferences.refreshToken)
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
//    }
//}
//
//@Module
//interface DesktopAppBindModule {
//    @Singleton
//    @Binds
//    fun bindAppVersionService(googleAppVersionService: FakeAppVersionService): AppVersionService
//}