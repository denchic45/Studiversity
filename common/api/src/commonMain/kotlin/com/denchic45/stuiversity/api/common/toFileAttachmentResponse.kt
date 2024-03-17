package com.denchic45.stuiversity.api.common


import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.util.toUUID
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun HttpResponse.toFileAttachmentResponse(): ResponseResult<FileAttachmentResponse> = toResult { response ->
    FileAttachmentResponse(
        response.headers["id"]!!.toUUID(),
        response.body(),
        ContentDisposition.parse(response.headers[HttpHeaders.ContentDisposition]!!)
            .parameter(ContentDisposition.Parameters.FileName)!!
    )
}