package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.GroupHeader
import javax.inject.Inject

class FindGroupByContainsNameUseCase @Inject constructor(
    groupRepository: GroupRepository
) : FindByContainsNameUseCase2<GroupHeader>(groupRepository)