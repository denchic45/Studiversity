package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import java.io.InputStream
import java.util.*

class UpdateAvatarUseCase(private val avatarService: AvatarService) {
    operator fun invoke(
        userId: UUID,
        inputStream: InputStream,
        extension: String
    ) = avatarService.updateAvatar(userId, inputStream, extension)
}