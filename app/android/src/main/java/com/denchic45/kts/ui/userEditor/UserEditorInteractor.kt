package com.denchic45.kts.ui.userEditor

import android.content.Context
import com.denchic45.avatarGenerator.AvatarGenerator
import com.denchic45.kts.AvatarBuilderTemplate
import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.data.repository.*
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.model.User.Companion.isStudent
import com.denchic45.kts.domain.model.User.Companion.isTeacher
import com.denchic45.kts.util.NetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserEditorInteractor @Inject constructor(
    context: Context,
    private val studyGroupRepository: StudyGroupRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val authService: AuthService,
    private val networkService: NetworkService
) : Interactor {

    private val avatarGenerator: AvatarGenerator.Builder = AvatarGenerator.Builder(context)

    private suspend fun loadAvatar(avatarBytes: ByteArray, id: String): String {
        return userRepository.updateUserAvatar(avatarBytes, id)
    }

    fun addUser(user: User): Flow<Resource<User>> {
        return flow {
            if (!networkService.isNetworkAvailable) {
                emit(Resource.Error(NetworkException()))
                return@flow
            }
            val photoUrl = createAvatar(user)
            val updatedUser = user.copy(photoUrl = photoUrl)
            emit(Resource.Next(updatedUser, "LOAD_AVATAR"))
            when {
                isStudent(updatedUser.role) -> studentRepository.add(updatedUser)
                isTeacher(updatedUser.role) -> teacherRepository.add(updatedUser)
            }
            emit(Resource.Success(updatedUser))
            return@flow
        }
    }

    fun updateUser(user: User): Flow<Resource<User>> = flow {
        if (!networkService.isNetworkAvailable) {
            emit(Resource.Error(NetworkException()))
        } else {
            val photoUrl = createAvatar(user)
            val updatedUser = user.copy(photoUrl = photoUrl)
            emit(Resource.Next(updatedUser, "LOAD_AVATAR"))
            when {
                isStudent(updatedUser.role) -> studentRepository.update(updatedUser)
                isTeacher(updatedUser.role) -> teacherRepository.update(updatedUser)
                else -> throw IllegalStateException()
            }
            emit(Resource.Success(updatedUser))
            return@flow
        }
    }

    private suspend fun createAvatar(user: User): String {
        val photoUrl: String = if (user.generatedAvatar) {
            val avatarBytes = avatarGenerator.name(user.firstName)
                .initFrom(AvatarBuilderTemplate())
                .generateBytes()
            loadAvatar(avatarBytes, user.id)
        } else {
            return user.photoUrl
        }
        return photoUrl
    }

    suspend fun removeTeacher(teacher: User) {
        return teacherRepository.remove(teacher)
    }

    fun observeUserById(userId: String): Flow<User?> {
        return userRepository.observeById(userId)
    }

    override fun removeListeners() {
        userRepository.removeListeners()
        studyGroupRepository.removeListeners()
    }

    suspend fun signUpUser(email: String, password: String) {
        authService.createNewUser(email, password)
    }

}