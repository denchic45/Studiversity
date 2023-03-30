package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.EmptyResource
import java.util.*

class RemoveUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(userId: UUID): EmptyResource {
        return userRepository.remove(userId)
    }
}