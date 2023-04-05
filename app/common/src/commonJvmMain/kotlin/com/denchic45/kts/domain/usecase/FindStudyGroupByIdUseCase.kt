package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindStudyGroupByIdUseCase @javax.inject.Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID): Resource<StudyGroupResponse> {
        return studyGroupRepository.findById(studyGroupId)
    }
}