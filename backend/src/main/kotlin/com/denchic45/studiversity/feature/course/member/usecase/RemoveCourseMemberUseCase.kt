package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class RemoveCourseMemberUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMemberRepository: CourseMemberRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(courseId: UUID, userId: UUID) = transactionWorker {
        courseMemberRepository.remove(courseId, userId)
        val nonExist = !courseMemberRepository.existMember(courseId, userId)
        if (nonExist) {
            roleRepository.removeUserRolesFromScope(userId, courseId)
        }
        nonExist
    }
}