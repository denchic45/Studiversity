package com.studiversity.client.di

import com.stuiversity.api.account.AccountApi
import com.stuiversity.api.account.AccountApiImpl
import com.stuiversity.api.auth.AuthApi
import com.stuiversity.api.auth.AuthApiImpl
import com.stuiversity.api.course.CoursesApi
import com.stuiversity.api.course.CourseApiImpl
import com.stuiversity.api.course.element.CourseElementApi
import com.stuiversity.api.course.element.CourseElementApiImpl
import com.stuiversity.api.course.subject.SubjectApi
import com.stuiversity.api.course.subject.SubjectApiImpl
import com.stuiversity.api.course.topic.CourseTopicApi
import com.stuiversity.api.course.topic.CourseTopicApiImpl
import com.stuiversity.api.course.work.CourseWorkApi
import com.stuiversity.api.course.work.CourseWorkApiImpl
import com.stuiversity.api.membership.MembershipsApi
import com.stuiversity.api.membership.MembershipsApiImpl
import com.stuiversity.api.role.CapabilityApi
import com.stuiversity.api.role.CapabilityApiImpl
import com.stuiversity.api.role.RoleApi
import com.stuiversity.api.role.RoleApiImpl
import com.stuiversity.api.room.RoomApi
import com.stuiversity.api.room.RoomApiImpl
import com.stuiversity.api.specialty.SpecialtyApi
import com.stuiversity.api.specialty.SpecialtyApiImpl
import com.stuiversity.api.studygroup.StudyGroupApi
import com.stuiversity.api.studygroup.StudyGroupApiImpl
import com.stuiversity.api.submission.SubmissionsApi
import com.stuiversity.api.submission.SubmissionsApiImpl
import com.stuiversity.api.timetable.TimetableApi
import com.stuiversity.api.timetable.TimetableApiImpl
import com.stuiversity.api.user.UserApi
import com.stuiversity.api.user.UserApiImpl
import org.koin.dsl.module

internal val apiModule = module {
    factory<AuthApi> { AuthApiImpl(it.get()) }
    factory<UserApi> { UserApiImpl(it.get()) }
    factory<AccountApi> { AccountApiImpl(it.get()) }
    factory<RoleApi> { RoleApiImpl(it.get()) }
    factory<CapabilityApi> { CapabilityApiImpl(it.get()) }
    factory<StudyGroupApi> { StudyGroupApiImpl(it.get()) }
    factory< SubjectApi> { SubjectApiImpl(it.get()) }
    factory<SpecialtyApi> { SpecialtyApiImpl(it.get()) }
    factory<CoursesApi> { CourseApiImpl(it.get()) }
    factory<CourseElementApi> { CourseElementApiImpl(it.get()) }
    factory<CourseWorkApi> { CourseWorkApiImpl(it.get()) }
    factory<SubmissionsApi> { SubmissionsApiImpl(it.get()) }
    factory<CourseTopicApi> { CourseTopicApiImpl(it.get()) }
    factory<MembershipsApi> { MembershipsApiImpl(it.get()) }
    factory<TimetableApi> { TimetableApiImpl(it.get()) }
    factory<RoomApi> { RoomApiImpl(it.get()) }
}