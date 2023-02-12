package com.studiversity.feature.studygroup

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.studygroup.repository.StudyGroupMemberRepository
import com.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.studiversity.feature.studygroup.usecase.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindStudyGroupByIdUseCase(get(), get()) }
    single { AddStudyGroupUseCase(get(named(OrganizationEnv.ORG_ID)), get(), get(), get(), get()) }
    single { UpdateStudyGroupUseCase(get()) }
    single { RemoveStudyGroupUseCase(get(), get(), get()) }
    single { RequireExistStudyGroupUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { StudyGroupRepository() }
    single { StudyGroupMemberRepository() }
}

val studyGroupModule = module {
    includes(useCaseModule, repositoryModule)
}