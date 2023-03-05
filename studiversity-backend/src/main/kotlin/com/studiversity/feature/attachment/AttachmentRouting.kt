package com.studiversity.feature.attachment

import com.studiversity.feature.attachment.usecase.FindAttachmentUseCase
import com.studiversity.ktor.currentUserId
import com.studiversity.ktor.getUuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureAttachments() {
    routing {
        authenticate("auth-jwt") {
            route("/attachments") {
                route("/{attachmentId}") {
                    val findAttachment: FindAttachmentUseCase by inject()

                    get {
                        val attachmentId = call.parameters.getUuid("attachmentId")
                        val currentUserId = call.currentUserId()
                        call.respond(HttpStatusCode.OK, findAttachment(attachmentId))
                    }
                }
            }
        }
    }
}