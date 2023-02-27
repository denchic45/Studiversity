package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindStudyGroupByIdUseCase @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID): Resource<StudyGroupResponse> {
        return studyGroupRepository.findById(studyGroupId)
    }
}