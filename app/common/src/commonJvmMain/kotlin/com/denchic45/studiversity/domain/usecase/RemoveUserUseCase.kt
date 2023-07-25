package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID): EmptyResource {
        return userRepository.remove(userId)
    }
}