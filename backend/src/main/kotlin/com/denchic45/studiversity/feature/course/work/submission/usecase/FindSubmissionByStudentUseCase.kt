package com.denchic45.studiversity.feature.course.work.submission.usecase

import com.denchic45.studiversity.feature.course.element.CourseElementRepository
import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.role.model.Role
import io.ktor.server.plugins.*
import java.util.*

class FindSubmissionByStudentUseCase(
    private val transactionWorker: TransactionWorker,
    private val submissionRepository: SubmissionRepository,
    private val courseElementRepository: CourseElementRepository,
    private val userMembershipRepository: UserMembershipRepository
) {

    operator fun invoke(courseWorkId: UUID, studentId: UUID, receivingUserId: UUID) = transactionWorker {
        submissionRepository.findByStudentId(courseWorkId, studentId)?.let { response ->
            if (response.state == SubmissionState.NEW && response.author.id == receivingUserId) {
                submissionRepository.updateSubmissionState(response.id, SubmissionState.CREATED)
                submissionRepository.find(response.id)!!
            } else response
        } ?: if (userMembershipRepository.existMemberByScopeIdAndRole(
                memberId = studentId,
                scopeId = courseElementRepository.findCourseIdByElementId(courseWorkId)!!,
                roleId = Role.Student.id
            )
        ) {
            if (studentId == receivingUserId) {
                submissionRepository.addCreatedSubmissionByStudentId(courseWorkId, studentId)
            } else {
                submissionRepository.addNewSubmissionByStudentId(courseWorkId, studentId)
            }
        } else throw NotFoundException()
    }
}