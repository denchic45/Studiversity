package com.studiversity.feature.course

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.course.element.courseElementModule
import com.studiversity.feature.course.repository.CourseRepository
import com.studiversity.feature.course.subject.subjectModule
import com.studiversity.feature.course.topic.courseTopicModule
import com.studiversity.feature.course.usecase.*
import com.studiversity.feature.course.work.submission.courseSubmissionModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddCourseUseCase(get(named(OrganizationEnv.ORG_ID)), get(), get(), get(), get()) }
    single { FindCourseByIdUseCase(get(), get()) }
    single { UpdateCourseUseCase(get()) }
    single { RequireExistCourseUseCase(get(), get()) }
    single { FindCourseStudyGroupsUseCase(get(), get()) }
    single { AttachStudyGroupToCourseUseCase(get(), get()) }
    single { DetachStudyGroupToCourseUseCase(get(), get()) }
    single { ArchiveCourseUseCase(get(), get()) }
    single { UnarchiveCourseUseCase(get(), get()) }
    single { RemoveCourseUseCase(get(), get(), get(), get()) }
}

private val repositoryModule = module { single { CourseRepository(get()) } }

val courseModule = module {
    includes(
        useCaseModule,
        repositoryModule,
        courseElementModule,
        courseSubmissionModule,
        courseTopicModule,
        subjectModule
    )
}