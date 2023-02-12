package com.stuiversity.api.course.work

import com.stuiversity.api.course.element.model.*
import com.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toAttachmentResult
import com.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File
import java.util.*

interface CourseWorkApi {
    suspend fun create(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest
    ): ResponseResult<CourseElementResponse>

    suspend fun getById(courseId: UUID, workId: UUID): ResponseResult<CourseElementResponse>

    suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID
    ): ResponseResult<List<AttachmentHeader>>

    suspend fun getAttachment(
        courseId: UUID,
        courseWorkId: UUID,
        attachmentId: UUID
    ): ResponseResult<Attachment>

    suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        file: File
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLinkToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun deleteAttachmentOfSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult
}

class CourseWorkApiImpl(private val client: HttpClient) : CourseWorkApi {
    override suspend fun create(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest
    ): ResponseResult<CourseElementResponse> {
        return client.post("/courses/$courseId/works") {
            contentType(ContentType.Application.Json)
            setBody(createCourseWorkRequest)
        }.toResult()
    }

    override suspend fun getById(courseId: UUID, workId: UUID): ResponseResult<CourseElementResponse> {
        return client.get("/courses/$courseId/works/$workId").toResult()
    }

    override suspend fun getAttachments(
        courseId: UUID,
        courseWorkId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/courses/${courseId}/works/${courseWorkId}/attachments")
            .toResult()
    }

    override suspend fun getAttachment(
        courseId: UUID,
        courseWorkId: UUID,
        attachmentId: UUID
    ): ResponseResult<Attachment> {
        return client.get("/courses/$courseId/works/$courseWorkId/attachments/$attachmentId").toAttachmentResult()
    }

    override suspend fun uploadFileToSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        file: File
    ): ResponseResult<FileAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/attachments") {
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
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(link)
        }.toResult()

    override suspend fun deleteAttachmentOfSubmission(
        courseId: UUID,
        courseWorkId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/$courseId/works/$courseWorkId/attachments/$attachmentId")
            .toResult()
    }
}