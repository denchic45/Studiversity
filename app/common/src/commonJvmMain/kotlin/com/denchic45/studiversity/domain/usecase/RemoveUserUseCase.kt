package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveUserUseCase @javax.inject.Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID): EmptyResource {
        return userRepository.remove(userId)
    }
}