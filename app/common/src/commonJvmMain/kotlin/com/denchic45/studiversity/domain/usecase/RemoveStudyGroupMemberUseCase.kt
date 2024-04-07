package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupMemberRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveStudyGroupMemberUseCase(
    private val studyGroupMemberRepository: StudyGroupMemberRepository
) {

    suspend operator fun invoke(studyGroupId: UUID, memberId: UUID): EmptyResource {
        return studyGroupMemberRepository.removeMember(studyGroupId, memberId)
    }
}