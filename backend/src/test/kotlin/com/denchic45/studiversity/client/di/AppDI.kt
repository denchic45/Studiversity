package com.denchic45.studiversity.client.di

import com.denchic45.stuiversity.api.account.AccountApi
import com.denchic45.stuiversity.api.account.AccountApiImpl
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.attachment.AttachmentApiImpl
import com.denchic45.stuiversity.api.auth.AuthApi
import com.denchic45.stuiversity.api.auth.AuthApiImpl
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
import org.koin.dsl.module

internal val apiModule = module {
    factory<AuthApi> { AuthApiImpl(it.get()) }
    factory<UserApi> { UserApiImpl(it.get()) }
    factory<AccountApi> { AccountApiImpl(it.get()) }
    factory<RoleApi> { RoleApiImpl(it.get()) }
    factory<CapabilityApi> { CapabilityApiImpl(it.get()) }
    factory<StudyGroupApi> { StudyGroupApiImpl(it.get()) }
    factory<ScheduleApi> { ScheduleApiImpl(it.get()) }
    factory<AttachmentApi> { AttachmentApiImpl(it.get()) }
    factory<SubjectApi> { SubjectApiImpl(it.get()) }
    factory<SpecialtyApi> { SpecialtyApiImpl(it.get()) }
    factory<CoursesApi> { CourseApiImpl(it.get()) }
    factory<CourseElementsApi> { CourseElementsApiImpl(it.get()) }
    factory<CourseWorkApi> { CourseWorkApiImpl(it.get()) }
    factory<SubmissionsApi> { SubmissionsApiImpl(it.get()) }
    factory<CourseTopicApi> { CourseTopicApiImpl(it.get()) }
    factory<MembershipApi> { MembershipApiImpl(it.get()) }
    factory<MembersApi> { MembersApiImpl(it.get()) }
    factory<TimetableApi> { TimetableApiImpl(it.get()) }
    factory<RoomApi> { RoomApiImpl(it.get()) }
}