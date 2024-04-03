package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupMemberRepository
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveStudyGroupMemberUseCase(
    private val studyGroupMemberRepository: StudyGroupMemberRepository
) {

    suspend operator fun invoke(userId: UUID, scopeId: UUID) {
        studyGroupMemberRepository.removeMember(userId, scopeId)
    }
}