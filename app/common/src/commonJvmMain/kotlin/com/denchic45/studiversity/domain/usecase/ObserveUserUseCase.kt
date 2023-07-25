package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.notNullOrFailure
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ObserveUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: UUID): Flow<Resource<UserResponse>> {
        return userRepository.observeById(userId).notNullOrFailure()
    }
}