package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindGroupMembersUseCase @Inject constructor(private val studyGroupRepository: StudyGroupRepository) {
    suspend operator fun invoke(groupId: UUID): Resource<List<ScopeMember>> {
        return studyGroupRepository.findGroupMembersByGroupId(groupId)
    }
}