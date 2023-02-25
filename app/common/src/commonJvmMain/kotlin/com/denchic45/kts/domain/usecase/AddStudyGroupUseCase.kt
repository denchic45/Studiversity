package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import javax.inject.Inject

class AddStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(createStudyGroupRequest: CreateStudyGroupRequest) {
        studyGroupRepository.add(createStudyGroupRequest)
    }
}