package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourStudyGroupsUseCase(
    private val studyGroupRepository: StudyGroupRepository
) {
    suspend operator fun invoke(): Resource<List<StudyGroupResponse>> {
        return studyGroupRepository.findByMe()
    }
}