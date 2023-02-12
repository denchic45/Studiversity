package com.studiversity.feature.user

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.user.account.usecase.UpdateAccountPersonalUseCase
import com.studiversity.feature.user.account.usecase.UpdateEmailUseCase
import com.studiversity.feature.user.account.usecase.UpdatePasswordUseCase
import com.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.studiversity.feature.user.usecase.RemoveUserUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindUserByIdUseCase(get(), get()) }
    single { RemoveUserUseCase(get(), get()) }

    single { UpdateAccountPersonalUseCase(get(), get()) }
    single { UpdateEmailUseCase(get(), get()) }
    single { UpdatePasswordUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { UserRepository(get(named(OrganizationEnv.ORG_ID))) }
}

val userModule = module {
    includes(useCaseModule, repositoryModule)
}