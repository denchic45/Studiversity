package com.denchic45.stuiversity.api.course.material

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import com.denchic45.stuiversity.util.UUIDWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import java.util.*

interface CourseMaterialApi {
    suspend fun create(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse>

    suspend fun update(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse>

    suspend fun getById(
        courseId: UUID,
        materialId: UUID
    ): ResponseResult<CourseMaterialResponse> // TODO: fix result in backend

    suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean? = null,
        submitted: Boolean? = null
    ): ResponseResult<List<CourseMaterialResponse>>

    suspend fun getAttachments(
        courseId: UUID,
        materialId: UUID
    ): ResponseResult<List<AttachmentHeader>>

//    suspend fun getAttachment(
//        courseId: UUID,
//       materialId: UUID,
//        attachmentId: UUID
//    ): ResponseResult<AttachmentResponse>

    suspend fun uploadFile(
        courseId: UUID,
        materialId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader>

    suspend fun addLink(
        courseId: UUID,
        materialId: UUID,
        request: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader>

    suspend fun deleteAttachment(
        courseId: UUID,
        materialId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult


}

class CourseMaterialApiImpl(private val client: HttpClient) : CourseMaterialApi {
    override suspend fun create(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse> {
        return client.post("/courses/$courseId/materials") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun update(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ): ResponseResult<CourseMaterialResponse> {
        return client.patch("/courses/$courseId/materials/$materialId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(
        courseId: UUID,
        materialId: UUID
    ): ResponseResult<CourseMaterialResponse> {
        return client.get("/courses/$courseId/materials/$materialId").toResult()
    }

    override suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean?,
        submitted: Boolean?
    ): ResponseResult<List<CourseMaterialResponse>> {
        return client.get("/course-works") {
            parameter("late", late)
            parameter("author_id", authorId.value)
            parameter("submitted", submitted)
        }.toResult()
    }

    override suspend fun getAttachments(
        courseId: UUID,
        materialId: UUID
    ): ResponseResult<List<AttachmentHeader>> {
        return client.get("/courses/${courseId}/works/${materialId}/attachments")
            .toResult()
    }

//    override suspend fun getAttachment(
//        courseId: UUID,
//       materialId: UUID,
//        attachmentId: UUID
//    ): ResponseResult<AttachmentResponse> {
//        return client.get("/courses/$courseId/works/$courseWorkId/attachments/$attachmentId").toAttachmentResult()
//    }

    @OptIn(InternalAPI::class)
    override suspend fun uploadFile(
        courseId: UUID,
        materialId: UUID,
        request: CreateFileRequest
    ): ResponseResult<FileAttachmentHeader> =
        client.post("/courses/$courseId/works/$materialId/attachments") {
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
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=${request.name}"
                            )
                        })
                    }
                )
            )
        }.toResult()

    override suspend fun addLink(
        courseId: UUID,
        materialId: UUID,
        request: CreateLinkRequest
    ): ResponseResult<LinkAttachmentHeader> =
        client.post("/courses/$courseId/works/$materialId/attachments") {
            parameter("upload", "link")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()

    override suspend fun deleteAttachment(
        courseId: UUID,
        materialId: UUID,
        attachmentId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/$courseId/materials/$materialId/attachments/$attachmentId")
            .toResult()
    }
}