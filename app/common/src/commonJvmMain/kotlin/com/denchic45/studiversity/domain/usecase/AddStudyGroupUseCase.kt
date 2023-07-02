package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddStudyGroupUseCase(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(createStudyGroupRequest: CreateStudyGroupRequest): Resource<StudyGroupResponse> {
        return studyGroupRepository.add(createStudyGroupRequest)
    }
}