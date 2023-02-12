package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.domain.model.GroupHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ObserveGroupInfoUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    operator fun invoke(groupId: String): Flow<GroupHeader> {
       return groupRepository.observeGroupInfoById(groupId)
    }
}