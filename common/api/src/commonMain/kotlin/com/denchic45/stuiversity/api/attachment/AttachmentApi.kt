package com.denchic45.stuiversity.api.attachment

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toFileAttachmentResponse
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
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
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun download(
        resource: String,
        resourceId: UUID,
        attachmentId: UUID
    ): ResponseResult<FileAttachmentResponse>

    suspend fun delete(
        resource: String,
        resourceId: UUID, attachmentId: UUID
    ): EmptyResponseResult
}

class AttachmentApiImpl(private val client: HttpClient) : AttachmentApi {
    override suspend fun download(
        resource: String,
        resourceId: UUID,
        attachmentId: UUID
    ): ResponseResult<FileAttachmentResponse> {
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

    override suspend fun uploadFile(
        resource: String,
        resourceId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> {
        return client.post("/attachments") {
            parameter("upload", "file")
            parameter("resource_type", resource)
            parameter("resource_id", resourceId)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun addLink(
        resource: String,
        resourceId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> {
        return client.post("/attachments") {
            parameter("upload", "link")
            parameter("resource_type", resource)
            parameter("resource_id", resourceId)
            contentType(ContentType.Application.Json)
            setBody(link)
        }.toResult()
    }

    override suspend fun delete(
        resource: String,
        resourceId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("/attachments").toResult()
    }

//    override suspend fun delete(attachmentId: UUID): EmptyResponseResult {
//        return client.delete("/attachments/$attachmentId").toResult()
//    }
}