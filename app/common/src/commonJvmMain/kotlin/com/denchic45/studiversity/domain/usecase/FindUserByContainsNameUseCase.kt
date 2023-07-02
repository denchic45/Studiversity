package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindUserByContainsNameUseCase(
    userRepository: UserRepository,
) : FindByContainsNameUseCase<UserResponse>(userRepository)