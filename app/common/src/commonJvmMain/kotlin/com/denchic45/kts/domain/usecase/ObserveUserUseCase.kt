package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.notNullOrFailure
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class ObserveUserUseCase @javax.inject.Inject constructor (private val userRepository: UserRepository) {
    operator fun invoke(userId: UUID): Flow<Resource<UserResponse>> {
        return userRepository.observeById(userId).notNullOrFailure()
    }
}