package com.denchic45.studiversity.feature.role

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.feature.role.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { RequireCapabilityUseCase(get(), get()) }
    single { RequireAvailableRolesInScopeUseCase(get()) }
    single { RequirePermissionToAssignRolesUseCase(get()) }
    single { FindRolesByNamesUseCase(get()) }
    single { FindAssignedUserRolesInScopeUseCase(get(), get()) }
    single { PutRoleToUserInScopeUseCase(get(), get()) }
    single { RemoveRoleFromUserInScopeUseCase(get(), get()) }
    single { FindMembersInScopeUseCase(get(), get()) }
    single { ExistMemberInScopeUseCase(get(), get()) }

    single { CheckUserCapabilitiesInScopeUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { RoleRepository() }
    single { ScopeRepository() }
}


val roleModule = module {
    includes(repositoryModule, useCaseModule)
}