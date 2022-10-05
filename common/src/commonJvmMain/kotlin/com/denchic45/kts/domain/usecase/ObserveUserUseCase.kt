package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: String) = userRepository.observeById(userId)
}