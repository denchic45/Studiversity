package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import com.denchic45.studiversity.feature.user.UserErrors
import com.denchic45.studiversity.feature.user.UserRepository
import io.ktor.server.plugins.*
import java.util.*

class ResetAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarService: AvatarService
) {
    operator fun invoke(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw NotFoundException(UserErrors.USER_NOT_FOUND)
        avatarService.resetAvatar(userId, user.firstName, user.surname)
    }
}