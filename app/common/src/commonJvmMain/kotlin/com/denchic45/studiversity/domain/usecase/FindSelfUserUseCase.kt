package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import me.tatarka.inject.annotations.Inject

@Inject
class FindSelfUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke() = userRepository.findSelf()
}