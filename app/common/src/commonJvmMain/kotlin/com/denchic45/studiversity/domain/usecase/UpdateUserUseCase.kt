package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID, request: UpdateUserRequest): Resource<UserResponse> {
        return userRepository.update(userId,request)
    }
}