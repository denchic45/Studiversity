package com.denchic45.kts.di

import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.attachment.AttachmentApiImpl
import com.denchic45.stuiversity.api.auth.AuthApi
import com.denchic45.stuiversity.api.auth.AuthApiImpl
import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import com.denchic45.stuiversity.api.course.CourseApiImpl
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApiImpl
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
import com.denchic45.stuiversity.util.ErrorResponse
import com.github.michaelbull.result.expect
import com.github.michaelbull.result.unwrapError
import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Named("guest")
    @Provides
    fun guestClient(appPreferences: AppPreferences): GuestHttpClient = HttpClient(Android) {
        defaultRequest {
            url("http://192.168.0.101:8080/")
        }
        installContentNegotiation()
    }

    @Provides
    fun authApi(@Named("guest") client: HttpClient): AuthApi = AuthApiImpl(client)

    @Singleton
    @Provides
    fun authedClient(appPreferences: AppPreferences): HttpClient = HttpClient(Android) {
        defaultRequest {
            url("http://192.168.0.101:8080/")
        }
        installContentNegotiation()
        install(WebSockets)
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(appPreferences.token!!, appPreferences.refreshToken!!)
                }
                refreshTokens {
                    val result = AuthApiImpl(client)
                        .refreshToken(RefreshTokenRequest(oldTokens!!.refreshToken))
                    val unwrapped = result.expect {
                        val unwrapError: ErrorResponse = result.unwrapError()
                        unwrapError.error.toString() + unwrapError.code
                    }
                    BearerTokens(unwrapped.token, unwrapped.refreshToken)
                }
            }
        }
    }

//  
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


    @Provides
    fun userApi(client: HttpClient): UserApi = UserApiImpl(client)

    @Provides
    fun capabilityApi(client: HttpClient): CapabilityApi = CapabilityApiImpl(client)

    @Provides
    fun courseApi(client: HttpClient): CoursesApi = CourseApiImpl(client)

    @Provides
    fun courseElementsApi(client: HttpClient): CourseElementsApi = CourseElementsApiImpl(client)

    @Provides
    fun courseWorkApi(client: HttpClient): CourseWorkApi = CourseWorkApiImpl(client)

    @Provides
    fun courseTopicApi(client: HttpClient): CourseTopicApi = CourseTopicApiImpl(client)

    @Provides
    fun studyGroupApi(client: HttpClient): StudyGroupApi = StudyGroupApiImpl(client)

    @Provides
    fun subjectApi(client: HttpClient): SubjectApi = SubjectApiImpl(client)

    @Provides
    fun specialtyApi(client: HttpClient): SpecialtyApi = SpecialtyApiImpl(client)

    @Provides
    fun memberApi(client: HttpClient): MembersApi = MembersApiImpl(client)

    @Provides
    fun membershipApi(client: HttpClient): MembershipApi = MembershipApiImpl(client)

    @Provides
    fun roleApi(client: HttpClient): RoleApi = RoleApiImpl(client)

    @Provides
    fun roomApi(client: HttpClient): RoomApi = RoomApiImpl(client)

    @Provides
    fun scheduleApi(client: HttpClient): ScheduleApi = ScheduleApiImpl(client)

    @Provides
    fun submissionApi(client: HttpClient): SubmissionsApi = SubmissionsApiImpl(client)

    @Provides
    fun timetableApi(client: HttpClient): TimetableApi = TimetableApiImpl(client)

    @Provides
    fun attachmentApi(client: HttpClient): AttachmentApi = AttachmentApiImpl(client)
//    @Provides
//    fun provideAppPreferences(): AppPreferences {
//        return AppPreferences(factory.createObservable("App"))
//    }
//
//    @Provides
//    fun provideUserPreferences(): UserPreferences {
//        return UserPreferences(factory.createObservable("User"))
//    }
//
//    @Provides
//    fun provideTimestampPreferences(): TimestampPreferences {
//        return TimestampPreferences(factory.createObservable("Timestamp"))
//    }
//
//    @Provides
//    fun provideCoursePreferences(): CoursePreferences {
//        return CoursePreferences(factory.createObservable("Courses"))
//    }
}