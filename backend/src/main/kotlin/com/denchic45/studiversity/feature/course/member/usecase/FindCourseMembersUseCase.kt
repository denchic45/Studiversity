package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindCourseMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMemberRepository: CourseMemberRepository,
) {
    operator fun invoke(courseId: UUID) = transactionWorker {
        courseMemberRepository.findByCourseId(courseId)
    }
}