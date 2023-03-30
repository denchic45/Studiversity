package com.denchic45.stuiversity.api.submission

import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.work.submission.model.GradeRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.util.orMe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File
import java.util.*

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

    suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<List<AttachmentHeader>>

    suspend fun getAttachment(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        attachmentId: UUID
    ): ResponseResult<AttachmentResponse>

    suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        file: File
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

    override suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/courses/${courseId}/works/${courseWorkId}/submissions/${submissionId}/attachments")
            .toResult()
    }

    override suspend fun getAttachment(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        attachmentId: UUID
    ): ResponseResult<AttachmentResponse> {
        return client.get("/courses/$courseId/works/$courseWorkId/submissions/$submissionId/attachments/$attachmentId")
            .toResult()
    }

    override suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID,
        file: File
    ): ResponseResult<FileAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/attachments") {
            parameter("upload", "file")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.defaultForFile(file))
                            append(HttpHeaders.ContentDisposition, "filename=${file.name}")
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
        return client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/submit").toResult()
    }

    override suspend fun cancelSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        submissionId: UUID
    ): ResponseResult<SubmissionResponse> {
        return client.post("/courses/$courseId/works/$courseWorkId/submissions/${submissionId}/cancel").toResult()
    }
}

