package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.DownloadAttachmentUseCase
import com.denchic45.studiversity.ktor.getUuidOrFail
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureAttachments() {
    routing {
        authenticate("auth-jwt") {
            route("/attachments") {
                val downloadAttachment: DownloadAttachmentUseCase by inject()

                post("/download") {
                    val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                    val fileSource = downloadAttachment(attachmentId)
                    call.respondAttachmentFile(fileSource)
                }
            }
        }
    }
}