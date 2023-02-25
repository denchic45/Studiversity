package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.stuiversity.api.user.model.UserResponse
import javax.inject.Inject

class FindUserByContainsNameUseCase @Inject constructor(
    userRepository: UserRepository,
) : FindByContainsNameUseCase<UserResponse>(userRepository)