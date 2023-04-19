package com.studiversity.feature.course.work.submission

import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.work.grade.GradeRequest
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.studiversity.feature.attachment.receiveAttachment
import com.studiversity.feature.attachment.respondAttachment
import com.studiversity.feature.course.work.submission.usecase.*
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.claimId
import com.studiversity.ktor.getUuidOrFail
import com.studiversity.ktor.jwtPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.workSubmissionRoutes() {
    route("/submissions") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findSubmissionsByWork: FindSubmissionsByWorkUseCase by inject()

        get {
            val courseId = call.parameters.getUuidOrFail("courseId")
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.ReadSubmissions,
                scopeId = courseId
            )

            val submissions = findSubmissionsByWork(courseId, call.parameters.getUuidOrFail("workId"))
            call.respond(HttpStatusCode.OK, submissions)
        }
        post { }
        submissionByIdRoute()
    }
    route("/submissionsByStudentId") {
        submissionByStudentIdRoute()
    }
}

fun Route.submissionByIdRoute() {
    route("/{submissionId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findSubmission: FindSubmissionUseCase by inject()
        val submitSubmission: SubmitSubmissionUseCase by inject()

        val addFileAttachmentOfSubmission: AddFileAttachmentOfSubmissionUseCase by inject()
        val addLinkAttachmentOfSubmission: AddLinkAttachmentOfSubmissionUseCase by inject()

        get {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val submission = findSubmission(
                call.parameters.getUuidOrFail("submissionId"),
                currentUserId
            )

            val isOwnSubmission = submission.author.id == currentUserId
            if (isOwnSubmission)
                call.respond(HttpStatusCode.OK, submission)
            else {
                requireCapability(
                    userId = currentUserId,
                    capability = Capability.ReadSubmissions,
                    scopeId = call.parameters.getUuidOrFail("courseId")
                )
                call.respond(HttpStatusCode.OK, submission)
            }
        }
        route("/attachments") {
            val requireSubmissionAuthor: RequireSubmissionAuthorUseCase by inject()
            val isSubmissionAuthor: IsSubmissionAuthorUseCase by inject()
            val findAttachmentsOfSubmission: FindAttachmentsOfSubmissionUseCase by inject()
            val removeAttachmentOfSubmission: RemoveAttachmentOfSubmissionUseCase by inject()

            post {
                val courseId = call.parameters.getUuidOrFail("courseId")
                val workId = call.parameters.getUuidOrFail("workId")
                val submissionId = call.parameters.getUuidOrFail("submissionId")
                val currentUserId = call.jwtPrincipal().payload.claimId

                requireSubmissionAuthor(submissionId, currentUserId)

                val result: AttachmentHeader = when (val attachment = receiveAttachment()) {
                    is CreateFileRequest -> addFileAttachmentOfSubmission(
                        submissionId = submissionId,
                        courseId = courseId,
                        workId = workId,
                        attachment = attachment
                    )

                    is CreateLinkRequest -> addLinkAttachmentOfSubmission(submissionId, attachment)
                }
                call.respond(HttpStatusCode.Created, result)
            }
            get {
                val courseId = call.parameters.getUuidOrFail("courseId")
                val submissionId = call.parameters.getUuidOrFail("submissionId")
                val currentUserId = call.jwtPrincipal().payload.claimId

                if (!isSubmissionAuthor(submissionId, currentUserId))
                    requireCapability(
                        userId = currentUserId,
                        capability = Capability.ReadSubmissions,
                        scopeId = courseId
                    )
                val attachments = findAttachmentsOfSubmission(submissionId)
                call.respond(HttpStatusCode.OK, attachments)
            }
            route("/{attachmentId}") {

                val findAttachmentOfSubmission: FindAttachmentOfSubmissionUseCase by inject()

                get {
                    val courseId = call.parameters.getUuidOrFail("courseId")
                    val workId = call.parameters.getUuidOrFail("workId")
                    val submissionId = call.parameters.getUuidOrFail("submissionId")
                    val attachmentId = call.parameters.getUuidOrFail("attachmentId")

                    requireCapability(
                        userId = call.jwtPrincipal().payload.claimId,
                        capability = Capability.ReadCourseElements,
                        courseId
                    )

                    val attachment = findAttachmentOfSubmission(courseId, workId, submissionId, attachmentId)
                    call.respondAttachment(attachment)
                }
                delete {
                    val courseId = call.parameters.getUuidOrFail("courseId")
                    val workId = call.parameters.getUuidOrFail("workId")
                    val submissionId = call.parameters.getUuidOrFail("submissionId")
                    val attachmentId = call.parameters.getUuidOrFail("attachmentId")
                    val currentUserId = call.jwtPrincipal().payload.claimId

                    requireSubmissionAuthor(submissionId, currentUserId)

                    removeAttachmentOfSubmission(
                        courseId = courseId,
                        elementId = workId,
                        submissionId = submissionId,
                        attachmentId = attachmentId
                    )
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
        route("/grade") {
            val setGradeSubmission: SetGradeSubmissionUseCase by inject()
            put {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuidOrFail("courseId")
                val workId = call.parameters.getUuidOrFail("workId")
                val submissionId = call.parameters.getUuidOrFail("submissionId")

                requireCapability(
                    userId = currentUserId,
                    capability = Capability.GradeSubmission,
                    scopeId = courseId
                )
                val body = call.receive<GradeRequest>()
                val submission = setGradeSubmission(
                    workId = workId,
                    grade = SubmissionGradeRequest(body.value, courseId, currentUserId, submissionId)
                )
                call.respond(HttpStatusCode.OK, submission)
            }
        }
        post("/submit") {
            val currentUserId = call.jwtPrincipal().payload.claimId
            requireCapability(
                userId = currentUserId,
                capability = Capability.SubmitSubmission,
                scopeId = call.parameters.getUuidOrFail("courseId")
            )
            val submittedSubmission = submitSubmission(
                submissionId = call.parameters.getUuidOrFail("submissionId"),
                studentId = currentUserId,
            )
            call.respond(HttpStatusCode.OK, submittedSubmission)
        }
        post {

        }
        delete {

        }
    }
}

fun Route.submissionByStudentIdRoute() {
    val requireCapability: RequireCapabilityUseCase by inject()
    val findSubmissionByStudent: FindSubmissionByStudentUseCase by inject()
    get("/{studentId}") {
        val currentUserId = call.jwtPrincipal().payload.claimId
        val submission = findSubmissionByStudent(
            call.parameters.getUuidOrFail("workId"),
            call.parameters.getUuidOrFail("studentId"),
            currentUserId
        )
        val isOwnSubmission = submission.author.id == currentUserId
        if (isOwnSubmission)
            call.respond(HttpStatusCode.OK, submission)
        else {
            requireCapability(
                userId = currentUserId,
                capability = Capability.ReadSubmissions,
                scopeId = call.parameters.getUuidOrFail("courseId")
            )
            call.respond(HttpStatusCode.OK, submission)
        }
    }
}