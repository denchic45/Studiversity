package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveUserUseCase @javax.inject.Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID): EmptyResource {
        return userRepository.remove(userId)
    }
}