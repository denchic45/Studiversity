package com.denchic45.kts.ui.studygroup

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StudyGroupInteractor @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
    private val userRepository: UserRepository
) : Interactor {

    val yourGroupId: String
        get() = studyGroupRepository.yourGroupId

    override fun removeListeners() {}

    val yourGroupName: String
        get() = studyGroupRepository.yourGroupName

    fun getNameByGroupId(groupId: String): Flow<String> {
        return studyGroupRepository.observeGroupInfoById(groupId).map { it.name }
    }

    fun isExistGroup(groupId: String): Flow<Boolean> {
        return studyGroupRepository.isExistGroup(groupId)
    }

    fun findThisUser(): User {
        return userRepository.findSelf()
    }
}