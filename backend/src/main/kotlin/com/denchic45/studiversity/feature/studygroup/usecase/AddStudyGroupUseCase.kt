package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.role.ScopeType
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.membership.model.CreateMembershipRequest
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.*

class AddStudyGroupUseCase(
    private val organizationId: UUID,
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val groupRepository: StudyGroupRepository,
    private val scopeRepository: ScopeRepository,
) {
  suspend operator fun invoke(request: CreateStudyGroupRequest): StudyGroupResponse = suspendTransactionWorker {
        groupRepository.add(request).also { response ->
            scopeRepository.add(response.id, ScopeType.StudyGroup, organizationId)
//            membershipRepository.addManualMembership(CreateMembershipRequest("manual", response.id))
        }
    }
}