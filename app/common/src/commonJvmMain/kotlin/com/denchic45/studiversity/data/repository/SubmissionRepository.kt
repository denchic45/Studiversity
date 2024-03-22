package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class SubmissionRepository(
    override val networkService: NetworkService,
    private val submissionsApi: SubmissionsApi
) : NetworkServiceOwner {
    suspend fun findByWorkAndStudent(
        workId: UUID,
        studentId: UUID
    ) = fetchResource { submissionsApi.getByStudent(workId, studentId) }

    suspend fun submitSubmission(submissionId: UUID) = fetchResource {
        submissionsApi.submitSubmission(submissionId)
    }

    suspend fun cancelSubmission(submissionId: UUID) = fetchResource {
        submissionsApi.cancelSubmission(submissionId)
    }

    suspend fun gradeSubmission(submissionId: UUID, grade: Int) = fetchResource {
        submissionsApi.gradeSubmission(submissionId, grade)
    }

    suspend fun removeSubmissionGrade(submissionId: UUID) = fetchResource {
        submissionsApi.cancelGradeSubmission(submissionId)
    }

    suspend fun findSubmissionsByWork(workId: UUID) = fetchResource {
        submissionsApi.getAllByCourseWorkId(workId)
    }

    fun findOwnSubmissionByWork(courseId: UUID, workId: UUID) = fetchResourceFlow {
        submissionsApi.getByStudent(courseId, workId)
    }

    suspend fun findById(submissionId: UUID) = fetchResource {
        submissionsApi.getById(submissionId)
    }
}
