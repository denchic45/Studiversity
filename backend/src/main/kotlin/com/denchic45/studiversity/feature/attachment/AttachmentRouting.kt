package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.FindAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentReferenceUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.FindAttachmentsByReferenceUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentResponse
import com.denchic45.stuiversity.util.toUUID
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
//                    val removeAttachmentReference: RemoveAttachmentReferenceUseCase by inject()
//                    val removeAttachment: RemoveAttachmentUseCase by inject()

                    get {
                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                        val currentUserId = call.currentUserId()
                        val response = findAttachment(attachmentId)

                        when (response) {
                            is FileAttachmentResponse -> {
                                call.response.header(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.Attachment.withParameter(
                                        ContentDisposition.Parameters.FileName,
                                        response.name
                                    ).toString()
                                )
                                call.response.header("id", response.id.toString())
                                call.respondBytes(response.bytes)
                            }

                            is LinkAttachmentResponse -> call.respond(HttpStatusCode.OK, response)
                        }

                    }
//                    delete {
//                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
//                        val consumerId = call.request.queryParameters["consumer_id"]?.toUUID()
//
//                        consumerId?.let {
//                            removeAttachmentReference(attachmentId, consumerId)
//                        } ?: removeAttachment(attachmentId)
//                        call.respond(HttpStatusCode.NoContent)
//                    }
                }
            }
        }
    }
}

fun Route.attachmentRoutes(
    ownerOfParameterName: String,
    beforePostAttachment: ApplicationCall.() -> Unit,
    beforeGetAttachments: ApplicationCall.() -> Unit,
    beforeDeleteAttachment: ApplicationCall.() -> Unit
) {
    route("/attachments") {
        val findAttachmentsByReference: FindAttachmentsByReferenceUseCase by inject()
        val addAttachmentUseCase: AddAttachmentUseCase by inject()
        val removeAttachment: RemoveAttachmentUseCase by inject()
        val removeAttachmentReference: RemoveAttachmentReferenceUseCase by inject()
        post {
            call.beforePostAttachment()
            val result = addAttachmentUseCase(receiveAttachment(), call.parameters.getUuidOrFail(ownerOfParameterName))
            call.respond(HttpStatusCode.Created, result)
        }
        get {
            call.beforeGetAttachments()
            val attachments = findAttachmentsByReference(call.parameters.getUuidOrFail(ownerOfParameterName))
            call.respond(HttpStatusCode.OK, attachments)
        }
        route("/{attachmentId}") {
            delete {
                call.beforeDeleteAttachment()

                val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                val consumerId = call.request.queryParameters["consumer_id"]?.toUUID()

                consumerId?.let {
                    removeAttachmentReference(attachmentId, consumerId)
                } ?: removeAttachment(attachmentId)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}