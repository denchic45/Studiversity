package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.Either
import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.data.repository.StudentRepository
import com.denchic45.kts.data.repository.TeacherRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.NetworkException
import me.tatarka.inject.annotations.Inject

@Inject
class AddUserUseCase(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val networkService: NetworkService,
    private val authService: AuthService,
) {

    suspend operator fun invoke(user: User, password: String): Result<User> {
        if (!networkService.isNetworkAvailable) {
            return Result.failure(NetworkException())
        }
        authService.createNewUser(user.email, password)
        val photoUrl = createAvatar(user)
        val updatedUser = user.copy(photoUrl = photoUrl)
        when {
            User.isStudent(updatedUser.role) -> studentRepository.add(updatedUser)
            User.isTeacher(updatedUser.role) -> teacherRepository.add(updatedUser)
        }
        return Result.success(updatedUser)

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