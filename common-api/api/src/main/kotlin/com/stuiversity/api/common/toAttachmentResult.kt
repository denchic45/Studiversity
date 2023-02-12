package com.stuiversity.api.common

import com.stuiversity.api.course.element.model.Attachment
import com.stuiversity.api.course.element.model.FileAttachment
import com.stuiversity.api.course.element.model.Link
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun HttpResponse.toAttachmentResult(): ResponseResult<Attachment> = toResult { response ->
    if (response.headers.contains(HttpHeaders.ContentDisposition)) {
        FileAttachment(
            response.body(),
            ContentDisposition.parse(response.headers[HttpHeaders.ContentDisposition]!!)
                .parameter(ContentDisposition.Parameters.FileName)!!
        )
    } else {
        response.body<Link>()
    }
}