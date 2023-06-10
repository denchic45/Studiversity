package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindStudyGroupByIdUseCase @javax.inject.Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    operator fun invoke(studyGroupId: UUID): Flow<Resource<StudyGroupResponse>> {
        return studyGroupRepository.findById(studyGroupId)
    }
}