package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.data.repository.StudyGroupMemberRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class AddStudyGroupMemberUseCase(
    private val studyGroupMemberRepository: StudyGroupMemberRepository
) {

    suspend operator fun invoke(studyGroupId: UUID, request: CreateMemberRequest): EmptyResource {
        return studyGroupMemberRepository.addMember(studyGroupId, request)
    }
}