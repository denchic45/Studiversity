package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID) {
        studyGroupRepository.remove(studyGroupId)
    }
}