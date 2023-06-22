package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class AddCourseWorkUseCase(
    private val transactionWorker: TransactionWorker,
    private val courseWorkRepository: CourseWorkRepository,
    private val userMembershipRepository: UserMembershipRepository,
    private val submissionRepository: SubmissionRepository
) {
    operator fun invoke(courseId: UUID, request: CreateCourseWorkRequest): CourseWorkResponse {
        val workId = transactionWorker {
            val workId = courseWorkRepository.add(courseId, request)
            val studentIds = userMembershipRepository.findMemberIdsByScopeAndRole(courseId, Role.Student.id)
            submissionRepository.addEmptySubmissionsByStudentIds(workId, studentIds)
            workId
        }
        return transactionWorker {
            courseWorkRepository.findWorkById(workId)
        }
    }
}