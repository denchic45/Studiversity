package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddUserUseCase @javax.inject.Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(createUserRequest: CreateUserRequest): Resource<UserResponse> {
        return userRepository.add(createUserRequest)
    }
}