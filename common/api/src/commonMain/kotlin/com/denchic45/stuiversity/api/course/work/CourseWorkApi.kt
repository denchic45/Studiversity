package com.denchic45.stuiversity.api.course.work

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toAttachmentResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.util.*

interface CourseWorkApi {
    suspend fun create(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest
    ): ResponseResult<CourseElementResponse>

    suspend fun update(
        courseId: UUID,
        workId:UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest
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
    ): ResponseResult<AttachmentResponse>

    suspend fun uploadFileToWork(
        courseId: UUID,
        courseWorkId: UUID,
        createFileRequest: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLinkToWork(
        courseId: UUID,
        courseWorkId: UUID,
        createLinkRequest: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun deleteAttachmentFromWork(
        courseId: UUID,
        workId: UUID,
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

    override suspend fun update(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ): ResponseResult<CourseElementResponse> {
        return client.post("/courses/$courseId/works/$workId") {
            contentType(ContentType.Application.Json)
            setBody(updateCourseWorkRequest)
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
    ): ResponseResult<AttachmentResponse> {
        return client.get("/courses/$courseId/works/$courseWorkId/attachments/$attachmentId").toAttachmentResult()
    }

    override suspend fun uploadFileToWork(
        courseId: UUID,
        courseWorkId: UUID,
        createFileRequest: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/attachments") {
            parameter("upload", "file")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", createFileRequest.bytes, Headers.build {
                            append(HttpHeaders.ContentType, ContentType.defaultForFilePath(createFileRequest.name))
                            append(HttpHeaders.ContentDisposition, "filename=${createFileRequest.name}")
                        })
                    }
                )
            )
        }.toResult()

    override suspend fun addLinkToWork(
        courseId: UUID,
        courseWorkId: UUID,
        createLinkRequest: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> =
        client.post("/courses/$courseId/works/$courseWorkId/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(createLinkRequest)
        }.toResult()

    override suspend fun deleteAttachmentFromWork(
        courseId: UUID,
        workId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/$courseId/works/$workId/attachments/$attachmentId")
            .toResult()
    }
}