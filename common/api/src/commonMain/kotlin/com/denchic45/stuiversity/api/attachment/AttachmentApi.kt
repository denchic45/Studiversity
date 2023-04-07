package com.denchic45.stuiversity.api.attachment

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toAttachmentResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import io.ktor.client.*
import io.ktor.client.request.*
import java.util.*

interface AttachmentApi {
    suspend fun getById(attachmentId: UUID): ResponseResult<AttachmentResponse>

    suspend fun delete(attachmentId: UUID): EmptyResponseResult
}

class AttachmentApiImpl(private val client: HttpClient) : AttachmentApi {
    override suspend fun getById(attachmentId: UUID): ResponseResult<AttachmentResponse> {
        return client.get("/attachments/$attachmentId").toAttachmentResult()
    }

    override suspend fun delete(attachmentId: UUID): EmptyResponseResult {
        return client.delete("/attachments/$attachmentId").toResult()
    }
}