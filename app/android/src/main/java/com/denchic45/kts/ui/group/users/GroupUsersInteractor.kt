package com.denchic45.kts.ui.group.users

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.data.repository.StudentRepository
import com.denchic45.kts.data.repository.UserRepository
import javax.inject.Inject

class GroupUsersInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val studentRepository: StudentRepository
) : Interactor {

    val yourGroupId: String
        get() = studyGroupRepository.yourGroupId

    suspend fun updateGroupCurator(groupId: String, teacherId: User) {
        studyGroupRepository.updateGroupCurator(groupId, teacherId)
    }

    override fun removeListeners() {
        studentRepository.removeListeners()
        userRepository.removeListeners()
        studyGroupRepository.removeListeners()
    }

    fun findThisUser(): User {
        return userRepository.findSelf()
    }
}