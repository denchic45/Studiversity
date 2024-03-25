package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.feature.auth.usecase.AddUserUseCase
import com.denchic45.studiversity.feature.user.account.usecase.*
import com.denchic45.studiversity.feature.user.usecase.FindUserAvatarUseCase
import com.denchic45.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.denchic45.studiversity.feature.user.usecase.RemoveUserUseCase
import com.denchic45.studiversity.feature.user.usecase.SearchUsersUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindUserByIdUseCase(get(), get()) }
    single { RemoveUserUseCase(get(), get()) }
    single { AddUserUseCase(get(), get(), get(), get()) }
    single { UpdateAccountPersonalUseCase(get(), get()) }
    single { UpdateEmailUseCase(get(), get()) }
    single { UpdatePasswordUseCase(get(), get()) }
    single { SearchUsersUseCase(get(), get()) }
    single { UpdateAvatarUseCase(get(), get()) }
    single { ResetAvatarUseCase(get()) }
    single { FindUserAvatarUseCase(get()) }
}

private val repositoryModule = module {
    single { UserRepository(get(), get()) }
}

val userModule = module {
    includes(useCaseModule, repositoryModule)
}