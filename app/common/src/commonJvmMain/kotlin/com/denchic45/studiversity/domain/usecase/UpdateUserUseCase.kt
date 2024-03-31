package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.user.model.UpdateUserRequest
import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID, request: UpdateUserRequest): EmptyResource {
        return userRepository.update(userId,request)
    }
}