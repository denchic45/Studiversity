package com.denchic45.studiversity.feature.user.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import java.util.*

class FindUserAvatarUseCase(private val avatarService: AvatarService) {
    operator fun invoke(userId: UUID) = avatarService.findByUserId(userId)

}