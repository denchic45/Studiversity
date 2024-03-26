package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.Resource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveStudyGroupUseCase(
    private val studyGroupRepository: StudyGroupRepository,
) {

    suspend operator fun invoke(studyGroupId: UUID): EmptyResource {
       return studyGroupRepository.remove(studyGroupId)
    }
}