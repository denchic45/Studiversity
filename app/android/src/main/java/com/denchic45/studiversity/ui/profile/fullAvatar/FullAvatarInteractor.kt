package com.denchic45.studiversity.ui.profile.fullAvatar

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.data.service.AvatarService
import com.denchic45.studiversity.domain.Interactor
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FullAvatarInteractor @javax.inject.Inject constructor(
    private val avatarService: AvatarService,
    private val userRepository: UserRepository,
) : Interactor {

    fun findThisUser(): UserResponse {
        return userRepository.findSelf()
    }

    suspend fun removeUserAvatar(user: UserResponse) {
        userRepository.removeAvatar(user.id)
    }

    override fun removeListeners() {}

}