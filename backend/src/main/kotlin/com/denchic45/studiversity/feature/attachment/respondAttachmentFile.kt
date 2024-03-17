package com.denchic45.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondAttachmentFile(response: FileAttachmentResponse) {
    this.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, response.name).toString()
    )
    this.response.header("id", response.id.toString())
    respondOutputStream(
        contentType = ContentType.defaultForFileExtension(response.name),
        producer = { response.inputStream.transferTo(this) }
    )
}