package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourStudyGroupsUseCase(
    private val studyGroupRepository: StudyGroupRepository,
) {
    operator fun invoke(): Flow<Resource<List<StudyGroupResponse>>> {
        return studyGroupRepository.findByMe()
    }
}