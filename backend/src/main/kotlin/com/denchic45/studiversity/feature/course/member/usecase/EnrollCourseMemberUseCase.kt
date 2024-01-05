package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import java.util.*

class EnrollCourseMemberUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMemberRepository: CourseMemberRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(courseId: UUID, request: CreateMemberRequest) = transactionWorker {
        courseMemberRepository.enroll(courseId, request.memberId)
        roleRepository.addUserRolesInScope(request.memberId, request.roleIds, courseId)
    }
}