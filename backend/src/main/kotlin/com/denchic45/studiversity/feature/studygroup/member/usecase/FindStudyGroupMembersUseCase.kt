package com.denchic45.studiversity.feature.studygroup.member.usecase

import com.denchic45.stuiversity.api.member.ScopeMembers
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.studygroup.member.StudyGroupMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.UUID

class FindStudyGroupMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupMemberRepository: StudyGroupMemberRepository,
    private val roleRepository: RoleRepository
) {
  operator fun invoke(studyGroupId:UUID) = transactionWorker {
      ScopeMembers(
          users = studyGroupMemberRepository.findByStudyGroupId(studyGroupId),
          userRoles = roleRepository.findUsersByScopeId(studyGroupId)
      )
  }
}