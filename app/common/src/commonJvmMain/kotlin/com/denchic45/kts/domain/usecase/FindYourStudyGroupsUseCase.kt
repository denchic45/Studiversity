package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
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