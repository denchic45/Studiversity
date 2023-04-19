package com.studiversity.feature.course.work.submission.usecase

import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.feature.course.work.submission.SubmissionRepository
import com.studiversity.transaction.TransactionWorker
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