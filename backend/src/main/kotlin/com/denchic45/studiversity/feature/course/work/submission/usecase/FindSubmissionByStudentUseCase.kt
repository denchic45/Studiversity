package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.feature.course.member.CourseMemberRepository
import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindSubmissionByStudentUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository,
    private val courseMemberRepository: CourseMemberRepository,
    private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(courseWorkId: UUID, studentId: UUID) = transactionWorker {
        if (!courseMemberRepository.existMember(
                courseElementRepository.findCourseIdByElementId(courseWorkId),
                studentId
            )
        ) throw NotFoundException()

        submissionRepository.findByStudentId(courseWorkId, studentId)
    }
}