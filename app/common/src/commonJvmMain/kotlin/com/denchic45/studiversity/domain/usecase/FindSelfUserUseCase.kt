package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSelfUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke() = userRepository.findSelf()
}