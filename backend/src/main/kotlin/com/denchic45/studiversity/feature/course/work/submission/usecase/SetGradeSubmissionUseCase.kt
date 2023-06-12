package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import io.ktor.server.plugins.*
import java.util.*

class SetGradeSubmissionUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository,
    private val courseElementRepository: CourseElementRepository
) {

    operator fun invoke(workId: UUID, grade: SubmissionGradeRequest) = transactionWorker {
//        val currentSubmission = submissionRepository.find(submissionId) ?: throw NotFoundException()
        if (courseElementRepository.findMaxGradeByWorkId(workId) < grade.value)
            throw BadRequestException("MAX_GRADE_LIMIT")
        submissionRepository.setGradeSubmission(grade)
    }
}