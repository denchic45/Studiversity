package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ObserveGroupNameByCuratorUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    operator fun invoke(groupId: String): Flow<String> {
       return groupRepository.observeGroupNameByCuratorId(groupId)
    }
}