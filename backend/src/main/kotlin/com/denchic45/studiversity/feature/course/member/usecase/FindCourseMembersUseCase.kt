package com.denchic45.studiversity.feature.course.member.usecase

import com.denchic45.studiversity.api.course.member.CourseMemberSorting
import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindCourseMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseMemberRepository: CourseMemberRepository,
) {
    operator fun invoke(courseId: UUID, sorting: List<CourseMemberSorting>?) = transactionWorker {
        courseMemberRepository.findByCourseId(courseId, sorting)
    }
}