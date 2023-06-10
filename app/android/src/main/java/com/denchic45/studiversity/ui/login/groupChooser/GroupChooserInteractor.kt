package com.denchic45.studiversity.ui.login.groupChooser


import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.channels.Channel
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class GroupChooserInteractor @javax.inject.Inject constructor(
    private val groupRepository: StudyGroupRepository
) {
    private val selectedGroup = Channel<UUID>()


    suspend fun findById(studyGroupId: UUID) {
        groupRepository.findById(studyGroupId)
    }

    suspend fun receiveSelectedGroupId(): UUID {
        return selectedGroup.receive()
    }

    suspend fun postSelectGroupId(studyGroupId:UUID) {
        selectedGroup.send(studyGroupId)
    }
}