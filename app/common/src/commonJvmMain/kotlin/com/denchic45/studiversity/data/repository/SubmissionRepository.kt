package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubmissionRepository @javax.inject.Inject constructor(
    override val networkService: NetworkService,
    private val submissionsApi: SubmissionsApi
) : NetworkServiceOwner {
    suspend fun findByWorkAndStudent(
        courseId: UUID, workId: UUID,
        studentId: UUID,
    ) = fetchResource { submissionsApi.getByStudent(courseId, workId, studentId) }

//    suspend fun updateByStudent(
//        courseId: UUID,
//        workId: UUID,
//        submissionId: UUID,
//        attachment: Attachment,
//    ) = fetchResource {
//        when (attachment) {
//            is AttachmentFile -> submissionsApi.uploadFileToSubmission(
//                courseId,
//                workId,
//                submissionId,
//                attachment
//            )
//            is AttachmentLink -> submissionsApi.addLinkToSubmission(
//                courseId,
//                workId,
//                submissionId,
//                CreateLinkRequest(attachment.url)
//            )
//        }
//    }

    suspend fun submitSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ) = fetchResource {
        submissionsApi.submitSubmission(courseId, workId, submissionId)
    }

    suspend fun cancelSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ) = fetchResource {
        submissionsApi.cancelSubmission(courseId, workId, submissionId)
    }

    suspend fun gradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        grade: Int,
    ) = fetchResource {
        submissionsApi.gradeSubmission(courseId, workId, submissionId, grade)
    }

    suspend fun removeSubmissionGrade(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ) = fetchResource {
        submissionsApi.cancelGradeSubmission(courseId, workId, submissionId)
    }

    suspend fun findSubmissionsByWork(courseId: UUID, workId: UUID) = fetchResource {
        submissionsApi.getAllByCourseWorkId(courseId, workId)
    }

    suspend fun findOwnSubmissionByWork(courseId: UUID, workId: UUID) = fetchResource {
        submissionsApi.getByStudent(courseId, workId)
    }

    suspend fun findById(courseId: UUID, workId: UUID, submissionId: UUID) = fetchResource {
        submissionsApi.getById(courseId, workId, submissionId)
    }
}