package com.denchic45.stuiversity.api.attachment

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toAttachmentResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface AttachmentApi {
    suspend fun getById(attachmentId: UUID): ResponseResult<AttachmentResponse>

    suspend fun getByResourceId(
        resourceType: String,
        resourceId: UUID
    ): ResponseResult<List<AttachmentHeader>>

    suspend fun uploadFile(
        resourceType: String,
        resourceId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLink(
        resourceType: String,
        resourceId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun delete(attachmentId: UUID): EmptyResponseResult

//    suspend fun delete(attachmentId: UUID): EmptyResponseResult
}

class AttachmentApiImpl(private val client: HttpClient) : AttachmentApi {
    override suspend fun getById(attachmentId: UUID): ResponseResult<AttachmentResponse> {
        return client.get("/attachments/$attachmentId").toAttachmentResult()
    }

    override suspend fun getByResourceId(
        resourceType: String,
        resourceId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/attachments") {
            parameter("resource_type", resourceType)
            parameter("resource_id", resourceId)
        }.toResult()
    }

    override suspend fun uploadFile(
        resourceType: String,
        resourceId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> {
        return client.post("/attachments") {
            parameter("upload", "file")
            parameter("resource_type", resourceType)
            parameter("resource_id", resourceId)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun addLink(
        resourceType: String,
        resourceId: UUID,
        link: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> {
        return client.post("/attachments") {
            parameter("upload", "link")
            parameter("resource_type", resourceType)
            parameter("resource_id", resourceId)
            contentType(ContentType.Application.Json)
            setBody(link)
        }.toResult()
    }

    override suspend fun delete(attachmentId: UUID): EmptyResponseResult {
        return client.delete("/attachments").toResult()
    }

//    override suspend fun delete(attachmentId: UUID): EmptyResponseResult {
//        return client.delete("/attachments/$attachmentId").toResult()
//    }
}