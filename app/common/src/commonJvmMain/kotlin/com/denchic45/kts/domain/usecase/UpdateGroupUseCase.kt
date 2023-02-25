package com.denchic45.kts.domain.usecase

import com.denchic45.kts.domain.model.Group
import com.denchic45.kts.data.repository.StudyGroupRepository
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository
) {

    suspend operator fun invoke(group: Group) {
        studyGroupRepository.update(group)
    }
}