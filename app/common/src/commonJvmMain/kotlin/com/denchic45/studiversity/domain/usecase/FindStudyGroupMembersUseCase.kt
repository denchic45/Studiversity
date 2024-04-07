package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.data.repository.StudyGroupMemberRepository
import com.denchic45.studiversity.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindStudyGroupMembersUseCase(private val studyGroupMemberRepository: StudyGroupMemberRepository) {
    operator fun invoke(groupId: UUID): Flow<Resource<List<ScopeMember>>> {
        return studyGroupMemberRepository.findMembersByStudyGroupId(groupId)
    }
}