package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.model.GroupHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ObserveGroupNameByCuratorUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository
) {

    operator fun invoke(groupId: String): Flow<GroupHeader> {
       return studyGroupRepository.observeGroupInfoByCuratorId(groupId)
    }
}