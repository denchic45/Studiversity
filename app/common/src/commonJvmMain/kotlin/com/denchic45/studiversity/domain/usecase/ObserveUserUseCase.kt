package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.user.model.UserResponse
import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ObserveUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: UUID): Flow<Resource<UserResponse?>> {
        return userRepository.observeById(userId)
    }
}