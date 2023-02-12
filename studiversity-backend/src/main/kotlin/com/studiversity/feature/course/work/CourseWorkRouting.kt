package com.studiversity.feature.course.work

import com.studiversity.feature.attachment.receiveAttachment
import com.studiversity.feature.attachment.respondAttachment
import com.studiversity.feature.course.element.usecase.*
import com.studiversity.feature.course.work.submission.workSubmissionRoutes
import com.studiversity.feature.course.work.usecase.AddCourseWorkUseCase
import com.stuiversity.api.role.model.Capability
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.claimId
import com.studiversity.ktor.getUuid
import com.studiversity.ktor.jwtPrincipal
import com.studiversity.util.toUUID
import com.stuiversity.api.course.element.model.AttachmentHeader
import com.stuiversity.api.course.element.model.CreateFileRequest
import com.stuiversity.api.course.element.model.CreateLinkRequest
import com.stuiversity.api.course.work.model.CreateCourseWorkRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.courseWorksRoutes() {
    route("/works") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val addCourseWork: AddCourseWorkUseCase by inject()
        post {
            val body: CreateCourseWorkRequest = call.receive()
            val courseId = call.parameters.getOrFail("courseId").toUUID()
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseWork,
                scopeId = courseId
            )
            addCourseWork(courseId, body).let { courseElement ->
                call.respond(courseElement)
            }
        }
        courseWorkById()
    }
}

private fun Route.courseWorkById() {
    route("/{workId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseElement: FindCourseElementUseCase by inject()

        get {
            val courseId = call.parameters.getOrFail("courseId").toUUID()

            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            val workId = findCourseElement(call.parameters.getOrFail("workId").toUUID())
            call.respond(HttpStatusCode.OK, workId)
        }
        route("/attachments") {

            val addFileAttachmentOfCourseElement: AddFileAttachmentOfCourseElementUseCase by inject()
            val addLinkAttachmentOfCourseElement: AddLinkAttachmentOfCourseElementUseCase by inject()
            val findCourseElementAttachments: FindAttachmentsOfCourseElementUseCase by inject()
            val removeAttachmentOfCourseElement: RemoveAttachmentOfCourseElementUseCase by inject()

            get {
                val workId = call.parameters.getUuid("workId")
                val courseId = call.parameters.getUuid("courseId")

                requireCapability(
                    userId = call.jwtPrincipal().payload.claimId,
                    capability = Capability.ReadCourseElements,
                    courseId
                )

                val attachments = findCourseElementAttachments(workId)
                call.respond(HttpStatusCode.OK, attachments)
            }
            post {
                val courseId = call.parameters.getUuid("courseId")
                val workId = call.parameters.getUuid("workId")
                val currentUserId = call.jwtPrincipal().payload.claimId

                requireCapability(
                    userId = currentUserId,
                    capability = Capability.WriteCourseWork,
                    scopeId = courseId
                )

                val result: AttachmentHeader = when (val attachment = receiveAttachment()) {
                    is CreateFileRequest -> addFileAttachmentOfCourseElement(
                        elementId = workId,
                        courseId = courseId,
                        attachment = attachment
                    )

                    is CreateLinkRequest -> addLinkAttachmentOfCourseElement(workId, attachment)
                }
                call.respond(HttpStatusCode.Created, result)
            }
            route("/{attachmentId}") {
                val findAttachmentOfCourseElement: FindAttachmentOfCourseElementUseCase by inject()

                get {
                    val courseId = call.parameters.getUuid("courseId")
                    val workId = call.parameters.getUuid("workId")
                    val attachmentId = call.parameters.getUuid("attachmentId")

                    requireCapability(
                        userId = call.jwtPrincipal().payload.claimId,
                        capability = Capability.ReadCourseElements,
                        courseId
                    )

                    val attachment = findAttachmentOfCourseElement(courseId, workId, attachmentId)
                    call.respondAttachment(attachment)
                }
                delete {
                    val courseId = call.parameters.getUuid("courseId")
                    val workId = call.parameters.getUuid("workId")
                    val attachmentId = call.parameters.getUuid("attachmentId")
                    val currentUserId = call.jwtPrincipal().payload.claimId

                    requireCapability(
                        userId = currentUserId,
                        capability = Capability.WriteCourseWork,
                        scopeId = courseId
                    )

                    removeAttachmentOfCourseElement(
                        courseId = courseId,
                        elementId = workId,
                        attachmentId = attachmentId
                    )
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
        workSubmissionRoutes()
    }
}