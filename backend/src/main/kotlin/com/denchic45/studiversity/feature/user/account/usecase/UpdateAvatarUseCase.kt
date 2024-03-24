package com.denchic45.studiversity.feature.user.account.usecase

import com.denchic45.studiversity.feature.user.AvatarService
import com.denchic45.studiversity.feature.user.UserRepository
import java.io.InputStream
import java.util.*

class UpdateAvatarUseCase(
    private val avatarService: AvatarService,
    private val userRepository: UserRepository
) {
    operator fun invoke(
        userId: UUID,
        inputStream: InputStream,
        extension: String
    ) = avatarService.setAvatar(userId, inputStream, extension)

}