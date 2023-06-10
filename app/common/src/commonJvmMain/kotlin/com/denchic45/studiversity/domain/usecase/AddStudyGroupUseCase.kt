package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(createStudyGroupRequest: CreateStudyGroupRequest): Resource<StudyGroupResponse> {
        return studyGroupRepository.add(createStudyGroupRequest)
    }
}