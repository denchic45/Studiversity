package com.denchic45.kts.data.repository

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import java.util.*

class SubmissionRepository(
    override val networkService: NetworkService,
    private val submissionsApi: SubmissionsApi,
) : NetworkServiceOwner {
    suspend fun findByWorkAndStudent(
        courseId: UUID, workId: UUID,
        studentId: UUID,
    ) = fetchResource { submissionsApi.getByStudent(courseId, workId, studentId) }

    suspend fun updateByStudent(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        attachment: Attachment,
    ) = fetchResource {
        submissionsApi.uploadFileToSubmission(
            courseId,
            workId,
            submissionId,
            attachment.file
        )
    }

    suspend fun gradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        grade: Short,
    ) = fetchResource {
        submissionsApi.gradeSubmission(courseId, workId, submissionId, grade)
    }

    suspend fun findSubmissionsByWork(courseId: UUID, workId: UUID) = fetchResource {
        submissionsApi.getAllByCourseWorkId(courseId, workId)
    }
}