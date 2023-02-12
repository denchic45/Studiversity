package com.studiversity.feature.studygroup.usecase

import com.studiversity.feature.membership.repository.MembershipRepository
import com.studiversity.feature.role.ScopeType
import com.studiversity.feature.role.repository.ScopeRepository
import com.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.stuiversity.api.studygroup.model.StudyGroupResponse
import com.studiversity.feature.studygroup.repository.StudyGroupRepository
import com.studiversity.transaction.TransactionWorker
import com.stuiversity.api.membership.model.CreateMembershipRequest
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