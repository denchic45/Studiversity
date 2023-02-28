package com.denchic45.stuiversity.api.common


import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentResponse
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun HttpResponse.toAttachmentResult(): ResponseResult<AttachmentResponse> = toResult { response ->
    if (response.headers.contains(HttpHeaders.ContentDisposition)) {
        FileAttachmentResponse(
            response.body(),
            ContentDisposition.parse(response.headers[HttpHeaders.ContentDisposition]!!)
                .parameter(ContentDisposition.Parameters.FileName)!!
        )
    } else {
        response.body<LinkAttachmentResponse>()
    }
}