package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import java.util.*
import javax.inject.Inject

class RemoveStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID) {
        studyGroupRepository.remove(studyGroupId)
    }
}