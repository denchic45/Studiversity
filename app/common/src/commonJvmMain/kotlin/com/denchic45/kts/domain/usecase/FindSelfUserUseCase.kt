package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSelfUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke() = userRepository.findSelf()
}