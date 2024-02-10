package com.denchic45.studiversity.feature.course

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.course.element.courseElementModule
import com.denchic45.studiversity.feature.course.member.courseMemberModule
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.feature.course.subject.subjectModule
import com.denchic45.studiversity.feature.course.topic.courseTopicModule
import com.denchic45.studiversity.feature.course.usecase.*
import com.denchic45.studiversity.feature.course.work.submission.courseSubmissionModule
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddCourseUseCase(config.organizationId, get(), get(), get()) }
    single { FindCourseByIdUseCase(get(), get()) }
    single { UpdateCourseUseCase(get(), get()) }
    single { RequireExistCourseUseCase(get(), get()) }
    single { FindCourseStudyGroupsUseCase(get(), get()) }
    single { AttachStudyGroupToCourseUseCase(get(), get()) }
    single { DetachStudyGroupToCourseUseCase(get(), get()) }
    single { ArchiveCourseUseCase(get(), get()) }
    single { UnarchiveCourseUseCase(get(), get()) }
    single { SearchCoursesUseCase(get(), get()) }
    single { RemoveCourseUseCase(get(), get(), get(), get()) }
}

private val repositoryModule = module { single { CourseRepository(get()) } }

val courseModule = module {
    includes(
        useCaseModule,
        repositoryModule,
        courseMemberModule,
        courseElementModule,
        courseSubmissionModule,
        courseTopicModule,
        subjectModule
    )
}