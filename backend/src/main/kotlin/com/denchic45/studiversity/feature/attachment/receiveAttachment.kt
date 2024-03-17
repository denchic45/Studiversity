package com.denchic45.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.element.model.UploadedAttachmentRequest
import com.denchic45.stuiversity.api.course.work.submission.SubmissionErrors
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.receiveAttachment(): AttachmentRequest {
    return when (call.request.queryParameters["upload"]) {
        "file" -> {
            val part = call.receiveMultipart().readPart()
                ?: throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)

            return if (part is PartData.FileItem) {
                val fileSourceName = part.originalFileName as String
                val inputStream = part.streamProvider()
                part.dispose()
                CreateFileRequest(fileSourceName, inputStream)
            } else throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)
        }

        "link" -> call.receive<CreateLinkRequest>()

        "attachment" -> call.receive<UploadedAttachmentRequest>()
        else -> throw BadRequestException(SubmissionErrors.INVALID_ATTACHMENT)
    }

}