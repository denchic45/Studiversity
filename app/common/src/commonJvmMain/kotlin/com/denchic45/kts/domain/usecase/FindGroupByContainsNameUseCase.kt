package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.model.GroupHeader
import javax.inject.Inject

class FindGroupByContainsNameUseCase @Inject constructor(
    studyGroupRepository: StudyGroupRepository
) : FindByContainsNameUseCase<GroupHeader>(studyGroupRepository)