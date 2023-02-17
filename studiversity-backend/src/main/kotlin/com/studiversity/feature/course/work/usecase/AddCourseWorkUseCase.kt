package com.studiversity.feature.course.work.usecase

import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.feature.course.work.submission.SubmissionRepository
import com.studiversity.feature.membership.repository.UserMembershipRepository
import com.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class AddCourseWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseElementRepository: CourseElementRepository,
    private val userMembershipRepository: UserMembershipRepository,
    private val submissionRepository: SubmissionRepository
) {
    operator fun invoke(courseId: UUID, request: CreateCourseWorkRequest) = transactionWorker {
        val response = courseElementRepository.addWork(courseId, request)
        val studentIds = userMembershipRepository.findMemberIdsByScopeAndRole(courseId, Role.Student.id)
        submissionRepository.addEmptySubmissionsByStudentIds(response.id, studentIds)
        response
    }
}