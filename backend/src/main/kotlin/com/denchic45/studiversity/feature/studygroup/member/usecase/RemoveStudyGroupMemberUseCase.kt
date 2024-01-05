package com.denchic45.studiversity.feature.studygroup.member.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.studygroup.member.StudyGroupMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class RemoveStudyGroupMemberUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupMemberRepository: StudyGroupMemberRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(courseId: UUID, userId: UUID): Unit = transactionWorker {
        studyGroupMemberRepository.remove(courseId, userId)
        roleRepository.removeUserRolesFromScope(userId, courseId)
    }
}