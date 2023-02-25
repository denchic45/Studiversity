package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(createUserRequest: CreateUserRequest): Resource<UserResponse> {
        return userRepository.add(createUserRequest)
    }
}