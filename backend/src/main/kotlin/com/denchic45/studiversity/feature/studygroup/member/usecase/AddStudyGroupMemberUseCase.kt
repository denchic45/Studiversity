package com.denchic45.studiversity.feature.studygroup.member.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.studygroup.member.StudyGroupMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import java.util.*

class AddStudyGroupMemberUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupMemberRepository: StudyGroupMemberRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(studyGroupId: UUID, request: CreateMemberRequest) = transactionWorker {
        studyGroupMemberRepository.add(studyGroupId, request.memberId)
        roleRepository.addUserRolesInScope(request.memberId, request.roleIds, studyGroupId)
    }
}