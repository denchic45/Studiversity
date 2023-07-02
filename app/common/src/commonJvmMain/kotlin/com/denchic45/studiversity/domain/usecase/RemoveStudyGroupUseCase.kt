package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveStudyGroupUseCase(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID) {
        studyGroupRepository.remove(studyGroupId)
    }
}