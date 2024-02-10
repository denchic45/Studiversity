package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.user.account.usecase.*
import com.denchic45.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.denchic45.studiversity.feature.user.usecase.RemoveUserUseCase
import com.denchic45.studiversity.feature.user.usecase.SearchUsersUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindUserByIdUseCase(get(), get()) }
    single { RemoveUserUseCase(get(), get()) }

    single { UpdateAccountPersonalUseCase(get(), get()) }
    single { UpdateEmailUseCase(get(), get()) }
    single { UpdatePasswordUseCase(get(), get()) }
    single { SearchUsersUseCase(get(), get()) }
    single { UpdateAvatarUseCase(get(), get()) }
    single { ResetAvatarUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { UserRepository(config.organizationId, get(), get()) }
}

val userModule = module {
    includes(useCaseModule, repositoryModule)
}