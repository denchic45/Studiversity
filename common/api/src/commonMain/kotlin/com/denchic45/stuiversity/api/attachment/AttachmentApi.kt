package com.denchic45.stuiversity.api.attachment

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toFileAttachmentResponse
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import java.util.*

interface AttachmentApi {
    suspend fun getByResourceId(
        resource: String,
        resourceId: UUID
    ): ResponseResult<List<AttachmentHeader>>

    suspend fun uploadFile(
        resource: String,
        resourceId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLink(
        resource: String,
        resourceId: UUID,
        request: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun addUploadedAttachment(
        resource: String,
        resourceId: UUID,
        request: UploadedAttachmentRequest
    ): ResponseResult<AttachmentHeader>

    suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse>

    suspend fun delete(
        resource: String,
        resourceId: UUID, attachmentId: UUID
    ): EmptyResponseResult
}

class AttachmentApiImpl(private val client: HttpClient) : AttachmentApi {
    override suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse> {
        return client.get("/attachments/$attachmentId").toFileAttachmentResponse()
    }

    override suspend fun getByResourceId(
        resource: String,
        resourceId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/attachments") {
            parameter("resource_type", resource)
            parameter("resource_id", resourceId)
        }.toResult()
    }

    @OptIn(InternalAPI::class)
    override suspend fun uploadFile(
        resource: String,
        resourceId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> {
        return client.post("$resource/$resourceId/attachments") {
            parameter("upload", "file")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", request.inputStream, Headers.build {
                            append(
                                HttpHeaders.ContentType,
                                ContentType.defaultForFilePath(request.name)
                            )
                            append(HttpHeaders.ContentDisposition, "filename=${request.name}")
                        })
                    }
                )
            )
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun addLink(
        resource: String,
        resourceId: UUID,
        request: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> {
        return client.post("$resource/$resourceId/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun addUploadedAttachment(
        resource: String,
        resourceId: UUID,
        request: UploadedAttachmentRequest
    ): ResponseResult<AttachmentHeader> {
        return client.post("$resource/$resourceId/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun delete(
        resource: String,
        resourceId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("$resource/$resourceId/attachments/$attachmentId").toResult()
    }
}