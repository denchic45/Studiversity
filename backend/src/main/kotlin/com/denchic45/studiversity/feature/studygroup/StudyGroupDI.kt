package com.denchic45.studiversity.feature.studygroup

import com.denchic45.studiversity.config
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupMemberRepository
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.feature.studygroup.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindStudyGroupByIdUseCase(get(), get()) }
    single { AddStudyGroupUseCase(config.organization.id, get(), get(), get(), get()) }
    single { UpdateStudyGroupUseCase(get(), get()) }
    single { SearchStudyGroupsUseCase(get(), get()) }
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