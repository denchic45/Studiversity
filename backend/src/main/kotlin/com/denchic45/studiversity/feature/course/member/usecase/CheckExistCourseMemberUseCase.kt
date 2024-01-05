package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import java.util.*

class CheckExistCourseMemberUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseMemberRepository: CourseMemberRepository
) {
    suspend operator fun invoke(courseId: UUID, memberId: UUID) = suspendTransactionWorker {
        courseMemberRepository.existMember(courseId, memberId)
    }
}