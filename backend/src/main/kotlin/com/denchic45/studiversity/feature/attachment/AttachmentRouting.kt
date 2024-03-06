package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.FindAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentReferenceUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.FindAttachmentsByReferenceUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.IsSubmissionAuthorUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.RequireSubmissionAuthorUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuid
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentResponse
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject


fun Application.configureAttachments() {
    routing {
        authenticate("auth-jwt") {
            route("/attachments") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val isSubmissionAuthor: IsSubmissionAuthorUseCase by inject()
                val requireSubmissionAuthor: RequireSubmissionAuthorUseCase by inject()
                val addAttachmentUseCase: AddAttachmentUseCase by inject()
                val findAttachmentsByReference: FindAttachmentsByReferenceUseCase by inject()

                val attachmentResources = mapOf(
                    AttachmentResource.SUBMISSION to lazy {
                        AttachmentResource.Submission(requireCapability, isSubmissionAuthor, requireSubmissionAuthor)
                    },
                    AttachmentResource.COURSE_WORK to lazy {
                        AttachmentResource.CourseWork(requireCapability)
                    }
                )

                post {
                    (attachmentResources[call.parameters.getOrFail("attachment_resource")]
                        ?: throw BadRequestException("UNKNOWN_ATTACHMENT_RESOURCE")).value
                        .requireAccessToAdd(call)

                    val resourceId = call.parameters.getUuidOrFail("resource_id")
                    val addedAttachment = addAttachmentUseCase(receiveAttachment(), resourceId)
                    call.respond(HttpStatusCode.Created, addedAttachment)
                }

                get {
                    (attachmentResources[call.parameters.getOrFail("attachment_resource")]
                        ?: throw BadRequestException("UNKNOWN_ATTACHMENT_RESOURCE")).value
                        .requireAccessToGet(call)

                    val resourceId = call.parameters.getUuidOrFail("resource_id")
                    val attachments = findAttachmentsByReference(resourceId)
                    call.respond(HttpStatusCode.OK, attachments)
                }

                route("/{attachmentId}") {
                    val findAttachment: FindAttachmentUseCase by inject()
                    val removeAttachmentReference: RemoveAttachmentReferenceUseCase by inject()
                    val removeAttachment: RemoveAttachmentUseCase by inject()

                    get {
                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                        when (val response = findAttachment(attachmentId)) {
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
                    delete {
                        (attachmentResources[call.parameters.getOrFail("attachment_resource")]
                            ?: throw BadRequestException("UNKNOWN_ATTACHMENT_RESOURCE")).value
                            .requireAccessToDelete(call)

                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                        val resourceId = call.parameters.getUuid("resource_id")

                        resourceId?.let { removeAttachmentReference(attachmentId, resourceId) }
                            ?: removeAttachment(attachmentId)

                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}


sealed interface AttachmentResource {
    companion object {
        const val SUBMISSION = "submission"
        const val COURSE_WORK = "course_work"
    }

    fun ApplicationCall.getResourceId() = parameters.getUuidOrFail("resource_id")

    fun requireAccessToAdd(call: ApplicationCall)

    fun requireAccessToGet(call: ApplicationCall)

    fun requireAccessToDelete(call: ApplicationCall)

    class Submission(
        private val requireCapability: RequireCapabilityUseCase,
        private val isSubmissionAuthor: IsSubmissionAuthorUseCase,
        private val requireSubmissionAuthor: RequireSubmissionAuthorUseCase
    ) : AttachmentResource {
        override fun requireAccessToAdd(call: ApplicationCall) {
            this@Submission.requireSubmissionAuthor(call.getResourceId(), call.currentUserId())
        }


        override fun requireAccessToGet(call: ApplicationCall) {
            if (!isSubmissionAuthor(call.parameters.getUuidOrFail("submissionId"), call.currentUserId()))
                requireCapability(
                    userId = call.currentUserId(),
                    capability = Capability.ReadSubmissions,
                    scopeId = call.parameters.getUuidOrFail("courseId")
                )
        }

        override fun requireAccessToDelete(call: ApplicationCall) {
            requireSubmissionAuthor(call.parameters.getUuidOrFail("submissionId"), call.currentUserId())
        }
    }

    class CourseWork(
        private val requireCapability: RequireCapabilityUseCase,
    ) : AttachmentResource {
        override fun requireAccessToAdd(call: ApplicationCall) {
            this@CourseWork.requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourseElements,
                scopeId = call.parameters.getUuidOrFail("courseId")
            )
        }

        override fun requireAccessToGet(call: ApplicationCall) {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.ReadCourseElements,
                call.parameters.getUuidOrFail("courseId")
            )
        }

        override fun requireAccessToDelete(call: ApplicationCall) {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourseElements,
                scopeId = call.parameters.getUuidOrFail("courseId")
            )
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