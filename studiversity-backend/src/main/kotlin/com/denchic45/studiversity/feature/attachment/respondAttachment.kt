package com.denchic45.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.AttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondAttachment(attachmentResponse: AttachmentResponse) {
    when (attachmentResponse) {
        is FileAttachmentResponse -> {
            response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, attachmentResponse.name)
                    .toString()
            )
            response.header("id",attachmentResponse.id.toString())
            respondBytes(
                bytes = attachmentResponse.bytes,
                contentType = ContentType.defaultForFileExtension(attachmentResponse.name),
                status = HttpStatusCode.OK
            )
        }

        is LinkAttachmentResponse -> respond(HttpStatusCode.OK, attachmentResponse)
    }
}