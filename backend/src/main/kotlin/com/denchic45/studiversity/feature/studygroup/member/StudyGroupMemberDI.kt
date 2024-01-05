package com.denchic45.studiversity.feature.studygroup.member

import com.denchic45.studiversity.feature.studygroup.member.usecase.AddStudyGroupMemberUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.CheckExistStudyGroupMemberUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.FindStudyGroupMembersUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.RemoveStudyGroupMemberUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddStudyGroupMemberUseCase(get(), get(), get()) }
    single { FindStudyGroupMembersUseCase(get(), get(), get()) }
    single { CheckExistStudyGroupMemberUseCase(get(), get()) }
    single { RemoveStudyGroupMemberUseCase(get(), get(), get()) }
}

private val repositoryModule = module {
    single { StudyGroupMemberRepository() }
}

val studyMemberModule = module {
    includes(useCaseModule, repositoryModule)
}