package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.feature.course.work.submission.SubmissionRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.role.model.Role
import java.util.*

class AddCourseWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseWorkRepository: CourseWorkRepository,
    private val submissionRepository: SubmissionRepository,
    private val roleRepository: RoleRepository
) {
  suspend operator fun invoke(courseId: UUID, request: CreateCourseWorkRequest): CourseWorkResponse {
        val workId = suspendTransactionWorker {
            val workId = courseWorkRepository.add(courseId, request)
            val studentIds = roleRepository.findUsersIdsByScopeIdAndRoleId(courseId, Role.Student.id)
            submissionRepository.addEmptySubmissionsByStudentIds(workId, studentIds)
            workId
        }
        return suspendTransactionWorker {
            courseWorkRepository.findWorkById(workId)
        }
    }
}