package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.repository.StudentRepository
import com.denchic45.kts.data.repository.TeacherRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.data.service.AvatarService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.error.NetworkError
import com.denchic45.kts.domain.model.User
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import me.tatarka.inject.annotations.Inject

@Inject
class AddUserUseCase(
    private val avatarService: AvatarService,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val networkService: NetworkService,
    private val authService: AuthService,
) {

    suspend operator fun invoke(user: User, password: String): Result<User, NetworkError> {
        if (!networkService.isNetworkAvailable) {
            return Err(NetworkError)
        }
        authService.createNewUser(user.email, password)
        val photoUrl = createAvatar(user)
        val updatedUser = user.copy(photoUrl = photoUrl)
        when (updatedUser.role) {
            UserRole.STUDENT -> studentRepository.add(updatedUser)
            UserRole.TEACHER, UserRole.HEAD_TEACHER -> teacherRepository.add(updatedUser)
        }
        return Ok(updatedUser)

    }

    private suspend fun createAvatar(user: User): String {
        return if (user.generatedAvatar) {
            val avatarBytes = avatarService.generateAvatar(user.firstName)
            loadAvatar(avatarBytes, user.id)
        } else {
            return user.photoUrl
        }
    }

    private suspend fun loadAvatar(avatarBytes: ByteArray, id: String): String {
        return userRepository.loadAvatar(avatarBytes, id)
    }
}