package com.denchic45.stuiversity.api.submission

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.work.grade.GradeRequest
import com.denchic45.stuiversity.api.submission.model.SubmissionByAuthor
import com.denchic45.stuiversity.api.submission.model.SubmissionResponse
import com.denchic45.stuiversity.util.orMe
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

interface SubmissionsApi {
    suspend fun getAllByCourseWorkId(workId: UUID): ResponseResult<List<SubmissionByAuthor>>

    suspend fun getByStudent(
        workId: UUID,
        userId: UUID? = null
    ): ResponseResult<SubmissionResponse>

    suspend fun getById(submissionId: UUID): ResponseResult<SubmissionResponse>

    suspend fun gradeSubmission(submissionId: UUID, grade: Int): ResponseResult<SubmissionResponse>

    suspend fun cancelGradeSubmission(submissionId: UUID): EmptyResponseResult

    suspend fun submitSubmission(submissionId: UUID): ResponseResult<SubmissionResponse>

    suspend fun cancelSubmission(submissionId: UUID): ResponseResult<SubmissionResponse>
}

class SubmissionsApiImpl(private val client: HttpClient) : SubmissionsApi {
    override suspend fun getAllByCourseWorkId(workId: UUID): ResponseResult<List<SubmissionByAuthor>> {
        return client.get("/course-works/${workId}/submissions").toResult()
    }

    override suspend fun getByStudent(
        workId: UUID,
        userId: UUID?
    ): ResponseResult<SubmissionResponse> {
        return client.get("/course-works/$workId/submissionsByStudentId/${userId.orMe}")
            .toResult()
    }

    override suspend fun getById(
        submissionId: UUID
    ): ResponseResult<SubmissionResponse> {
        return client.get("/work-submissions/$submissionId")
            .toResult()
    }

    override suspend fun gradeSubmission(
        submissionId: UUID,
        grade: Int,
    ): ResponseResult<SubmissionResponse> {
        return client.put("work-submissions/${submissionId}/grade") {
            contentType(ContentType.Application.Json)
            setBody(GradeRequest(grade))
        }.toResult()
    }

    override suspend fun cancelGradeSubmission(submissionId: UUID): EmptyResponseResult {
        return client.delete("/work-submissions/${submissionId}/grade") {
            contentType(ContentType.Application.Json)
        }.toResult()
    }

    override suspend fun submitSubmission(submissionId: UUID): ResponseResult<SubmissionResponse> {
        return client.post("/work-submissions/${submissionId}/submit").toResult()
    }

    override suspend fun cancelSubmission(submissionId: UUID): ResponseResult<SubmissionResponse> {
        return client.post("/work-submissions/${submissionId}/cancel").toResult()
    }
}