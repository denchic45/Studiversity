package com.denchic45.stuiversity.api.submission

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentHeader
import com.denchic45.stuiversity.api.course.work.grade.GradeRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.util.orMe
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.defaultForFilePath
import java.util.UUID

interface SubmissionsApi {
    suspend fun getAllByCourseWorkId(
        courseId: UUID,
        workId: UUID
    ): ResponseResult<List<SubmissionResponse>>

    suspend fun getByStudent(
        courseId: UUID,
        workId: UUID,
        userId: UUID? = null
    ): ResponseResult<SubmissionResponse>

    suspend fun getById(
        courseId: UUID,
        courseWorkId: UUID, submissionId: UUID
    ): ResponseResult<SubmissionResponse>

    suspend fun gradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        grade: Int
    ): ResponseResult<SubmissionResponse>

    suspend fun cancelGradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ): EmptyResponseResult

    suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<List<AttachmentHeader>>

    suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLinkToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun deleteAttachmentOfSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult

    suspend fun submitSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse>

    suspend fun cancelSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse>
}

class SubmissionsApiImpl(private val client: HttpClient) : SubmissionsApi {
    override suspend fun getAllByCourseWorkId(
        courseId: UUID,
        workId: UUID
    ): ResponseResult<List<SubmissionResponse>> {
        return client.get("/courses/${courseId}/works/${workId}/submissions").toResult()
    }

    override suspend fun getByStudent(
        courseId: UUID,
        workId: UUID,
        userId: UUID?
    ): ResponseResult<SubmissionResponse> {
        return client.get("/courses/$courseId/works/$workId/submissionsByStudentId/${userId.orMe}")
            .toResult()
    }

    override suspend fun getById(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse> {
        return client.get("/courses/$courseId/works/$courseWorkId/submissions/$submissionId")
            .toResult()
    }

    override suspend fun gradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        grade: Int,
    ): ResponseResult<SubmissionResponse> {
        return client.put("/courses/${courseId}/works/${workId}/submissions/${submissionId}/grade") {
            contentType(ContentType.Application.Json)
            setBody(GradeRequest(grade))
        }.toResult()
    }

    override suspend fun cancelGradeSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/${courseId}/works/${workId}/submissions/${submissionId}/grade") {
            contentType(ContentType.Application.Json)
        }.toResult()
    }

    override suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/courses/${courseId}/works/${courseWorkId}/submissions/${submissionId}/attachments")
            .toResult()
    }

    override suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/attachments") {
            parameter("upload", "file")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", request.bytes, Headers.build {
                            append(
                                HttpHeaders.ContentType,
                                ContentType.defaultForFilePath(request.name)
                            )
                            append(HttpHeaders.ContentDisposition, "filename=${request.name}")
                        })
                    }
                )
            )
        }.toResult()

    override suspend fun addLinkToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(link)
        }.toResult()

    override suspend fun deleteAttachmentOfSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/attachments/$attachmentId")
            .toResult()
    }

    override suspend fun submitSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse> {
        return client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/submit")
            .toResult()
    }

    override suspend fun cancelSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse> {
        return client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/cancel")
            .toResult()
    }
}

