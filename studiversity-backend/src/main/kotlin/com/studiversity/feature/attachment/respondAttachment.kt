package com.studiversity.feature.attachment

import com.stuiversity.api.course.element.model.Attachment
import com.stuiversity.api.course.element.model.FileAttachment
import com.stuiversity.api.course.element.model.Link
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondAttachment(attachment: Attachment) {
    when (attachment) {
        is FileAttachment -> {
            response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, attachment.name)
                    .toString()
            )
            respondBytes(
                bytes = attachment.bytes,
                contentType = ContentType.defaultForFileExtension(attachment.name),
                status = HttpStatusCode.OK
            )
        }

        is Link -> respond(HttpStatusCode.OK, attachment)
    }
}