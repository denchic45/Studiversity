package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.feature.auth.usecase.AddUserUseCase
import com.denchic45.studiversity.feature.user.account.usecase.*
import com.denchic45.studiversity.feature.user.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindUserByIdUseCase(get(), get()) }
    single { RemoveUserUseCase(get(), get(), get()) }
    single { AddUserUseCase(get(), get(), get(), get()) }
    single { UpdateUserUseCase(get(), get(), get()) }
    single { UpdateAccountPersonalUseCase(get(), get()) }
    single { UpdateEmailUseCase(get(), get(), get(), get()) }
    single { UpdatePasswordUseCase(get(), get()) }
    single { SearchUsersUseCase(get(), get()) }
    single { UpdateAvatarUseCase(get(), get()) }
    single { ResetAvatarUseCase(get(), get()) }
    single { FindUserAvatarUseCase(get()) }
}

private val repositoryModule = module {
    single { UserRepository(get(), get()) }
    single { TokenRepository() }
}

val userModule = module {
    single { AvatarService(get()) }
    includes(useCaseModule, repositoryModule)
}