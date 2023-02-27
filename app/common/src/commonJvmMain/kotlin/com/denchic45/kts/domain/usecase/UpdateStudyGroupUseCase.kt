package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import java.util.*
import javax.inject.Inject

class UpdateStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest,
    ): Resource<StudyGroupResponse> {
        return studyGroupRepository.update(studyGroupId, updateStudyGroupRequest)
    }
}