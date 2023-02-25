package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.model.GroupMembers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindGroupMembersUseCase @Inject constructor(private val studyGroupRepository: StudyGroupRepository) {
    operator fun invoke(groupId: String): Flow<GroupMembers> {
        return studyGroupRepository.findGroupMembersByGroupId(groupId)
    }
}