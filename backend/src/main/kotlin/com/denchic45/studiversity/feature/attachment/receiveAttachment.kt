package com.denchic45.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.work.submission.SubmissionErrors
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.receiveAttachment(): AttachmentRequest {
    return when (call.request.queryParameters["upload"]) {
        "file" -> {
            call.receiveMultipart().readPart()?.let { part ->
                if (part is PartData.FileItem) {
                    val fileSourceName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()

                    CreateFileRequest(fileSourceName, fileBytes)
                } else throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)
            } ?: throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)
        }

        "link" -> call.receive<CreateLinkRequest>()
        else -> throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)
    }
}