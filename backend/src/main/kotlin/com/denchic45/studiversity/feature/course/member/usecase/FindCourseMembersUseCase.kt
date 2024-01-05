package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.stuiversity.api.member.ScopeMembers
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.UUID

class FindCourseMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMemberRepository: CourseMemberRepository,
    private val roleRepository: RoleRepository
) {
  operator fun invoke(courseId:UUID) = transactionWorker {
      ScopeMembers(
          users = courseMemberRepository.findByCourseId(courseId),
          userRoles = roleRepository.findUsersByScopeId(courseId)
      )
  }
}