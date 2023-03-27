package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindGroupMembersUseCase constructor(private val studyGroupRepository: StudyGroupRepository) {
    suspend operator fun invoke(groupId: UUID): Resource<List<ScopeMember>> {
        return studyGroupRepository.findGroupMembersByGroupId(groupId)
    }
}