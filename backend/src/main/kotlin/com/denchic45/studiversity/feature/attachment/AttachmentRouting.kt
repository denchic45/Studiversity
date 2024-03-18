package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.DownloadAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.FindAttachmentsByResourceUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.IsSubmissionAuthorUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.RequireSubmissionAuthorUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.attachment.AttachmentResource
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject


fun Application.configureAttachments() {
    routing {
        authenticate("auth-jwt") {
            route("/attachments") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val isSubmissionAuthor: IsSubmissionAuthorUseCase by inject()
                val requireSubmissionAuthor: RequireSubmissionAuthorUseCase by inject()
                val addAttachment: AddAttachmentUseCase by inject()
                val findAttachmentsByReference: FindAttachmentsByResourceUseCase by inject()
                val downloadAttachment: DownloadAttachmentUseCase by inject()

                val attachmentResourceRequirements = mapOf(
                    AttachmentResource.SUBMISSION to lazy {
                        AttachmentResourceRequirements.Submission(
                            requireCapability,
                            isSubmissionAuthor,
                            requireSubmissionAuthor
                        )
                    },
                    AttachmentResource.COURSE_WORK to lazy {
                        AttachmentResourceRequirements.CourseWork(requireCapability)
                    }
                )

                post("/download") {
                    val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                    val fileSource = downloadAttachment(attachmentId)
                    call.respondAttachmentFile(fileSource)
                }

                fun PipelineContext<Unit, ApplicationCall>.getAttachmentResourceRequirements(resourceType: String?): AttachmentResourceRequirements {
                    val type = resourceType ?: call.parameters.getOrFail("resource_type")
                    return (attachmentResourceRequirements[type]
                        ?: throw BadRequestException("UNKNOWN_ATTACHMENT_RESOURCE")).value
                }

                post {
                    getAttachmentResourceRequirements(null).requireAccessToAdd(call)

                    val resourceId = call.parameters.getUuidOrFail("resource_id")
                    val addedAttachment = addAttachment(receiveAttachment(), resourceId)
                    call.respond(HttpStatusCode.Created, addedAttachment)
                }

                get {
                    getAttachmentResourceRequirements(null).requireAccessToGet(call)

                    val resourceId = call.parameters.getUuidOrFail("resource_id")
                    val attachments = findAttachmentsByReference(resourceId)
                    call.respond(HttpStatusCode.OK, attachments)
                }

//                route("/{attachmentId}") {
//                    val findAttachment: FindAttachmentUseCase by inject()
//                    val removeAttachmentReference: RemoveAttachmentReferenceUseCase by inject()
//                    val removeAttachment: RemoveAttachmentUseCase by inject()
//
//                    get {
//                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
//                        when (val response = findAttachment(attachmentId)) {
//                            is FileAttachmentResponse -> {
//                                call.response.header(
//                                    HttpHeaders.ContentDisposition,
//                                    ContentDisposition.Attachment.withParameter(
//                                        ContentDisposition.Parameters.FileName,
//                                        response.name
//                                    ).toString()
//                                )
//                                call.response.header("id", response.id.toString())
//                                call.respondBytes(response.inputStream)
//                            }
//
//                            is LinkAttachmentResponse -> call.respond(HttpStatusCode.OK, response)
//                        }
//                    }
//                    delete {
//                        val attachmentId = call.parameters.getUuidOrFail("attachmentId")
//                        val resourceId = call.parameters.getUuid("resource_id")
//
//                        getAttachmentResourceRequirements(findAttachmentResourceTypeById(attachmentId))
//                            .requireAccessToDelete(call)
//
////                        removeAttachment(attachmentId)
//
////                        resourceId?.let { removeAttachmentReference(attachmentId, resourceId) }
////                            ?: removeAttachment(attachmentId) todo !!!
//
//                        call.respond(HttpStatusCode.NoContent)
//                    }
//
//                    delete("/reference") {
//                        // TODO: реализовать удаление ссылки на attachment
//                    }
//                }
            }
        }
    }
}


sealed interface AttachmentResourceRequirements {


    fun ApplicationCall.getResourceId() = parameters.getUuidOrFail("resource_id")

    fun requireAccessToAdd(call: ApplicationCall)

    fun requireAccessToGet(call: ApplicationCall)

    fun requireAccessToDelete(call: ApplicationCall)

    class Submission(
        private val requireCapability: RequireCapabilityUseCase,
        private val isSubmissionAuthor: IsSubmissionAuthorUseCase,
        private val requireSubmissionAuthor: RequireSubmissionAuthorUseCase
    ) : AttachmentResourceRequirements {
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
    ) : AttachmentResourceRequirements {
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