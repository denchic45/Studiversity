package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveHeadmanUseCase @Inject constructor(private val studyGroupRepository: StudyGroupRepository) {

    suspend operator fun invoke(groupId: String) {
        studyGroupRepository.removeHeadman(groupId)
    }
}