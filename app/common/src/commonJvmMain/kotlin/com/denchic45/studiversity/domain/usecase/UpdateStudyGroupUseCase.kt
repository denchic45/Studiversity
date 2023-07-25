package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateStudyGroupUseCase(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest,
    ): Resource<StudyGroupResponse> {
        return studyGroupRepository.update(studyGroupId, updateStudyGroupRequest)
    }
}