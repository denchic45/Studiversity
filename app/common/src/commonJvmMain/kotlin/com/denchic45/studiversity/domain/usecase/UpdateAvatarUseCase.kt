package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateAvatarUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID, request: CreateFileRequest): Resource<String> {
        return userRepository.updateAvatar(userId, request)
    }
}