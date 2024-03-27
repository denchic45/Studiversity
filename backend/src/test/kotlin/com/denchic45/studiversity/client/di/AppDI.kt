package com.denchic45.studiversity.client.di

import com.denchic45.studiversity.api.account.AccountApi
import com.denchic45.studiversity.api.account.AccountApiImpl
import com.denchic45.studiversity.api.attachment.AttachmentApi
import com.denchic45.studiversity.api.attachment.AttachmentApiImpl
import com.denchic45.studiversity.api.auth.AuthApi
import com.denchic45.studiversity.api.auth.AuthApiImpl
import com.denchic45.studiversity.api.course.CourseApiImpl
import com.denchic45.studiversity.api.course.CourseApi
import com.denchic45.studiversity.api.course.element.CourseElementsApi
import com.denchic45.studiversity.api.course.element.CourseElementsApiImpl
import com.denchic45.studiversity.api.course.subject.SubjectApi
import com.denchic45.studiversity.api.course.subject.SubjectApiImpl
import com.denchic45.studiversity.api.course.topic.CourseTopicApi
import com.denchic45.studiversity.api.course.topic.CourseTopicApiImpl
import com.denchic45.studiversity.api.course.work.CourseWorkApi
import com.denchic45.studiversity.api.course.work.CourseWorkApiImpl
import com.denchic45.studiversity.api.member.MembersApi
import com.denchic45.studiversity.api.member.MembersApiImpl
import com.denchic45.studiversity.api.membership.MembershipApi
import com.denchic45.studiversity.api.membership.MembershipApiImpl
import com.denchic45.studiversity.api.role.CapabilityApi
import com.denchic45.studiversity.api.role.CapabilityApiImpl
import com.denchic45.studiversity.api.role.RoleApi
import com.denchic45.studiversity.api.role.RoleApiImpl
import com.denchic45.studiversity.api.room.RoomApi
import com.denchic45.studiversity.api.room.RoomApiImpl
import com.denchic45.studiversity.api.schedule.ScheduleApi
import com.denchic45.studiversity.api.schedule.ScheduleApiImpl
import com.denchic45.studiversity.api.specialty.SpecialtyApi
import com.denchic45.studiversity.api.specialty.SpecialtyApiImpl
import com.denchic45.studiversity.api.studygroup.StudyGroupApi
import com.denchic45.studiversity.api.studygroup.StudyGroupApiImpl
import com.denchic45.studiversity.api.submission.SubmissionsApi
import com.denchic45.studiversity.api.submission.SubmissionsApiImpl
import com.denchic45.studiversity.api.timetable.TimetableApi
import com.denchic45.studiversity.api.timetable.TimetableApiImpl
import com.denchic45.studiversity.api.user.UserApi
import com.denchic45.studiversity.api.user.UserApiImpl
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
    factory<CourseApi> { CourseApiImpl(it.get()) }
    factory<CourseElementsApi> { CourseElementsApiImpl(it.get()) }
    factory<CourseWorkApi> { CourseWorkApiImpl(it.get()) }
    factory<SubmissionsApi> { SubmissionsApiImpl(it.get()) }
    factory<CourseTopicApi> { CourseTopicApiImpl(it.get()) }
    factory<MembershipApi> { MembershipApiImpl(it.get()) }
    factory<MembersApi> { MembersApiImpl(it.get()) }
    factory<TimetableApi> { TimetableApiImpl(it.get()) }
    factory<RoomApi> { RoomApiImpl(it.get()) }
}