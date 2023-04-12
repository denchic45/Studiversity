package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindStudyGroupUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    operator fun invoke(groupId: UUID): Flow<Resource<StudyGroupResponse>> {
        return studyGroupRepository.observeById(groupId)
    }

}