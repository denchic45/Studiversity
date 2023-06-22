package com.denchic45.studiversity.di

import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.attachment.AttachmentApiImpl
import com.denchic45.stuiversity.api.auth.AuthApi
import com.denchic45.stuiversity.api.auth.AuthApiImpl
import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import com.denchic45.stuiversity.api.course.CourseApiImpl
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApiImpl
import com.denchic45.stuiversity.api.course.material.CourseMaterialApi
import com.denchic45.stuiversity.api.course.material.CourseMaterialApiImpl
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.SubjectApiImpl
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.topic.CourseTopicApiImpl
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.CourseWorkApiImpl
import com.denchic45.stuiversity.api.member.MembersApi
import com.denchic45.stuiversity.api.member.MembersApiImpl
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.denchic45.stuiversity.api.membership.MembershipApiImpl
import com.denchic45.stuiversity.api.role.CapabilityApi
import com.denchic45.stuiversity.api.role.CapabilityApiImpl
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.RoleApiImpl
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.RoomApiImpl
import com.denchic45.stuiversity.api.schedule.ScheduleApi
import com.denchic45.stuiversity.api.schedule.ScheduleApiImpl
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.SpecialtyApiImpl
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.StudyGroupApiImpl
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import com.denchic45.stuiversity.api.submission.SubmissionsApiImpl
import com.denchic45.stuiversity.api.timetable.TimetableApi
import com.denchic45.stuiversity.api.timetable.TimetableApiImpl
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.UserApiImpl
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@LayerScope
@Component
abstract class NetworkComponent(
    @get:Provides val engine: HttpClientEngineFactory<*>,
) {
    @LayerScope
    @Provides
    fun guestClient(): GuestHttpClient = HttpClient(engine) {
        installContentNegotiation()
    }

    @LayerScope
    @Provides
    fun authApi(client: HttpClient): AuthApi = AuthApiImpl(client)

    @LayerScope
    @Provides
    fun authedClient(appPreferences: AppPreferences): HttpClient = HttpClient(engine) {
        println("TOKENS: authedClient")
        defaultRequest {
            println("URL REQUEST: ${appPreferences.url} $this")
            url(appPreferences.url)
        }
        installContentNegotiation()
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("HTTP Client: $message")
                }
            }
            level = LogLevel.ALL
        }
        install(WebSockets)
        install(Auth) {
            bearer {
                sendWithoutRequest { request ->
                    val b = request.url.host == Url(appPreferences.url).host
                    println("sendWithoutRequest:$b")
                    b
                }
                loadTokens {
                    println("TOKENS: appPreferences.token ${appPreferences.token}")
                    println("TOKENS: appPreferences.refreshToken ${appPreferences.refreshToken}")
                    BearerTokens(appPreferences.token, appPreferences.refreshToken)
                }
                refreshTokens {
                    println("refresh tokens... ${this.oldTokens?.refreshToken}")
                    val result = AuthApiImpl(client)
                        .refreshToken(RefreshTokenRequest(appPreferences.refreshToken))
                    result.onSuccess {
                        appPreferences.token = it.token
                        appPreferences.refreshToken = it.refreshToken
                        println("SUCCESS refresh tokens: ${appPreferences.token}")
                    }.onFailure {
                        println("FAILURE refresh tokens: ${it.error}")
                        appPreferences.token = ""
                        appPreferences.refreshToken = ""
                    }
                    BearerTokens(appPreferences.token, appPreferences.refreshToken)
                }
            }
        }
    }

//    @LayerScope
//    @Provides
//    fun provideHttpClient() = HttpClient {
//        install(Logging) {
//            level = LogLevel.ALL
//        }
//        install(ContentNegotiation) {
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//                ignoreUnknownKeys = true
//                encodeDefaults = true
//            })
//        }
//    }

    @LayerScope
    @Provides
    fun userApi(client: HttpClient): UserApi = UserApiImpl(client)

    @LayerScope
    @Provides
    fun capabilityApi(client: HttpClient): CapabilityApi = CapabilityApiImpl(client)

    @LayerScope
    @Provides
    fun courseApi(client: HttpClient): CoursesApi = CourseApiImpl(client)

    @LayerScope
    @Provides
    fun courseTopicApi(client: HttpClient): CourseTopicApi = CourseTopicApiImpl(client)

    @LayerScope
    @Provides
    fun studyGroupApi(client: HttpClient): StudyGroupApi = StudyGroupApiImpl(client)

    @LayerScope
    @Provides
    fun subjectApi(client: HttpClient): SubjectApi = SubjectApiImpl(client)

    @LayerScope
    @Provides
    fun specialtyApi(client: HttpClient): SpecialtyApi = SpecialtyApiImpl(client)

    @LayerScope
    @Provides
    fun memberApi(client: HttpClient): MembersApi = MembersApiImpl(client)

    @LayerScope
    @Provides
    fun membershipApi(client: HttpClient): MembershipApi = MembershipApiImpl(client)

    @LayerScope
    @Provides
    fun roleApi(client: HttpClient): RoleApi = RoleApiImpl(client)

    @LayerScope
    @Provides
    fun roomApi(client: HttpClient): RoomApi = RoomApiImpl(client)

    @LayerScope
    @Provides
    fun scheduleApi(client: HttpClient): ScheduleApi = ScheduleApiImpl(client)

    @LayerScope
    @Provides
    fun submissionApi(client: HttpClient): SubmissionsApi = SubmissionsApiImpl(client)

    @LayerScope
    @Provides
    fun timetableApi(client: HttpClient): TimetableApi = TimetableApiImpl(client)

    @LayerScope
    @Provides
    fun courseElementsApi(client: HttpClient): CourseElementsApi = CourseElementsApiImpl(client)

    @LayerScope
    @Provides
    fun courseWorkApi(client: HttpClient): CourseWorkApi = CourseWorkApiImpl(client)

    @LayerScope
    @Provides
    fun courseMaterialApi(client: HttpClient): CourseMaterialApi = CourseMaterialApiImpl(client)

    @LayerScope
    @Provides
    fun attachmentApi(client: HttpClient): AttachmentApi = AttachmentApiImpl(client)

//    @LayerScope
//    @Provides
//    fun provideFirebaseHttpClient(
//        httpClient: HttpClient,
//        appPreferences: AppPreferences,
//    ): FirebaseHttpClient = HttpClient {
//        install(Logging) {
//            level = LogLevel.ALL
//        }
//        install(ContentNegotiation) {
//
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//                ignoreUnknownKeys = true
//                encodeDefaults = true
//            })
//        }
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
//                    val response: RefreshTokenResponse =
//                        httpClient.submitForm(
//                            url = "https://securetoken.googleapis.com/v1/token",
//                            formParameters = Parameters.build {
//                                append("grant_type", "refresh_token")
//                                append("refresh_token", oldTokens?.refreshToken ?: "")
//                            }) {
//                            parameter("key", ApiKeys.firebaseApiKey)
//                            markAsRefreshTokenRequest()
//                        }.body()
//
//                    appPreferences.apply {
//                        token = response.id_token
//                        refreshToken = response.refresh_token
//                    }
//                    BearerTokens(response.id_token, response.refresh_token)
//                }
//            }
//        }
//    }
}

typealias GuestHttpClient = HttpClient

fun HttpClientConfig<out HttpClientEngineConfig>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

typealias FirebaseHttpClient = HttpClient