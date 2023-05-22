package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.Resource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveAvatarUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID): Resource<String> {
        return userRepository.removeAvatar(userId)
    }
}