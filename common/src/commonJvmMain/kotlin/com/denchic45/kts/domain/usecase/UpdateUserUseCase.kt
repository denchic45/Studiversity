package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.data.repository.StudentRepository
import com.denchic45.kts.data.repository.TeacherRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.error.NetworkError
import com.denchic45.kts.domain.model.User
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateUserUseCase(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val networkService: NetworkService,
    private val authService: AuthService,
) {

    suspend operator fun invoke(user: User): Result<User, NetworkError> {
        return if (!networkService.isNetworkAvailable) {
            Err(NetworkError)
        } else {
            val photoUrl = createAvatar(user)
            val updatedUser = user.copy(photoUrl = photoUrl)
            when (updatedUser.role) {
                UserRole.STUDENT -> studentRepository.update(updatedUser)
                UserRole.TEACHER, UserRole.HEAD_TEACHER -> teacherRepository.update(updatedUser)
            }
            Ok(updatedUser)
        }
    }

    private suspend fun createAvatar(user: User): String {

        TODO("")

//        val photoUrl: String = if (user.generatedAvatar) {
//            val avatarBytes = avatarGenerator.name(user.firstName)
//                .initFrom(AvatarBuilderTemplate())
//                .generateBytes()
//            loadAvatar(avatarBytes, user.id)
//        } else {
//            return user.photoUrl
//        }
//        return photoUrl
    }

    private suspend fun loadAvatar(avatarBytes: ByteArray, id: String): String {
        return userRepository.loadAvatar(avatarBytes, id)
    }
}