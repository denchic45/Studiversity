package com.denchic45.kts.ui.profile

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.model.Group
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.data.repository.StudentRepository
import com.denchic45.kts.data.repository.TeacherRepository
import com.denchic45.kts.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val studyGroupRepository: StudyGroupRepository
) : Interactor {

    fun observe(id: String): Flow<User?> {
        return userRepository.observeById(id)
    }

    fun findThisUser(): User {
        return userRepository.findSelf()
    }

    fun findGroupByStudent(user: User): Flow<Group> {
        return studyGroupRepository.findGroupByStudent(user)
    }

    fun findGroupByCurator(user: User): Flow<Group?> {
        return studyGroupRepository.findGroupByCuratorId(user.id)
    }

    override fun removeListeners() {
        userRepository.removeListeners()
        studyGroupRepository.removeListeners()
    }

    suspend fun updateAvatar(user: User, imageBytes: ByteArray) {
        val photoUrl = userRepository.updateUserAvatar(imageBytes, user.id)
        val updatedUser = user.copy(photoUrl = photoUrl, generatedAvatar = false)
        when {
            updatedUser.isStudent -> {
                studentRepository.update(updatedUser)
            }
            updatedUser.isTeacher -> {
                teacherRepository.update(updatedUser)
            }
        }
    }
}