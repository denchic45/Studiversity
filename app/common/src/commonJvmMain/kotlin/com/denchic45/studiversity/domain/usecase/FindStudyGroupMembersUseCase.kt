package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.data.repository.StudyGroupMemberRepository
import com.denchic45.studiversity.domain.resource.Resource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindStudyGroupMembersUseCase(private val studyGroupMemberRepository: StudyGroupMemberRepository) {
    suspend operator fun invoke(groupId: UUID): Resource<List<ScopeMember>> {
        return studyGroupMemberRepository.findMembersByStudyGroupId(groupId)
    }
}