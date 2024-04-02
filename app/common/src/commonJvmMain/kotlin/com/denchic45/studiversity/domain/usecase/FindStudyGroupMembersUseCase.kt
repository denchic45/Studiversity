package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.membership.model.ScopeMember
import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.domain.resource.Resource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindStudyGroupMembersUseCase(private val studyGroupRepository: StudyGroupRepository) {
    suspend operator fun invoke(groupId: UUID): Resource<List<ScopeMember>> {
        return studyGroupRepository.findStudyGroupMembersByGroupId(groupId)
    }
}