package com.denchic45.studiversity.feature.studygroup.usecase

import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.feature.role.ScopeType
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.membership.model.CreateMembershipRequest
import java.util.*

class AddStudyGroupUseCase(
    private val organizationId: UUID,
    private val transactionWorker: TransactionWorker,
    private val groupRepository: StudyGroupRepository,
    private val scopeRepository: ScopeRepository,
    private val membershipRepository: MembershipRepository
) {
    operator fun invoke(request: CreateStudyGroupRequest): StudyGroupResponse = transactionWorker {
        groupRepository.add(request).also { response ->
            scopeRepository.add(response.id, ScopeType.StudyGroup, organizationId)
            membershipRepository.addManualMembership(CreateMembershipRequest("manual", response.id))
        }
    }
}