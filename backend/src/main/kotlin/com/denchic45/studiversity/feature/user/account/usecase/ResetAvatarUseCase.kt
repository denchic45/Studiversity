package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import com.denchic45.studiversity.feature.user.UserRepository
import java.util.*

class ResetAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarService: AvatarService
) {
    operator fun invoke(userId: UUID) = avatarService.resetAvatar(userId, userRepository.findUserNameById(userId))
}