package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.domain.model.GroupMembers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindGroupMembersUseCase @Inject constructor(private val groupRepository: GroupRepository) {
    operator fun invoke(groupId: String): Flow<GroupMembers> {
        return groupRepository.findGroupMembersByGroupId(groupId)
    }
}