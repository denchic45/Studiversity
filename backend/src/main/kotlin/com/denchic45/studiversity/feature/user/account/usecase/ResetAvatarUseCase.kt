package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import java.util.*

class ResetAvatarUseCase(private val avatarService: AvatarService) {
    suspend operator fun invoke(userId: UUID) = avatarService.resetAvatar(userId)
}