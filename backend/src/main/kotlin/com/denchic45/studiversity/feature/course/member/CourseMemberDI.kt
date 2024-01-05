package com.denchic45.studiversity.feature.course.member

import com.denchic45.studiversity.feature.course.member.usecase.CheckExistCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.EnrollCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.FindCourseMembersUseCase
import com.denchic45.studiversity.feature.course.member.usecase.RemoveCourseMemberUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { EnrollCourseMemberUseCase(get(), get(), get()) }
    single { FindCourseMembersUseCase(get(), get(), get()) }
    single { CheckExistCourseMemberUseCase(get(), get()) }
    single { RemoveCourseMemberUseCase(get(), get(), get()) }
}

private val repositoryModule = module {
    single { CourseMemberRepository() }
}

val courseMemberModule = module {
    includes(useCaseModule, repositoryModule)
}