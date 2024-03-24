package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.studiversity.feature.attachment.receiveAttachment
import com.denchic45.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.FindAttachmentsByResourceUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentUseCase
import com.denchic45.studiversity.feature.course.work.submission.usecase.*
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUserUuidByParameterOrMe
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.course.work.grade.GradeRequest
import com.denchic45.stuiversity.api.course.work.grade.SubmissionGradeRequest
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.submissionRoutes() {
    routing {
        authenticate("auth-jwt") {
            val requireCapability: RequireCapabilityUseCase by inject()
            val findSubmissionsByWork: FindSubmissionsByWorkUseCase by inject()
            get("/course-works/{workId}/submissions") {
                val courseId = call.parameters.getUuidOrFail("courseId")
                requireCapability(
                    userId = call.currentUserId(),
                    capability = Capability.ReadSubmissions,
                    scopeId = courseId
                )

                val submissions = findSubmissionsByWork(courseId, call.parameters.getUuidOrFail("workId"))
                call.respond(HttpStatusCode.OK, submissions)
            }

            route("/submissions") {
                post { }
                submissionByIdRoute()
            }
            submissionByAuthorRoute()
        }
    }
}

fun Route.submissionByIdRoute() {
    route("/{submissionId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findSubmission: FindSubmissionUseCase by inject()
        val submitSubmission: SubmitSubmissionUseCase by inject()
        val cancelSubmission: CancelSubmissionUseCase by inject()
        val requireSubmissionAuthor: RequireSubmissionAuthorUseCase by inject()
        val isSubmissionAuthor: IsSubmissionAuthorUseCase by inject()

        get {
            val currentUserId = call.currentUserId()
            val submission = findSubmission(
                call.parameters.getUuidOrFail("submissionId"),
                currentUserId
            )

            val isOwnSubmission = submission.author.id == currentUserId
            if (isOwnSubmission) call.respond(HttpStatusCode.OK, submission)
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
            val addAttachment: AddAttachmentUseCase by inject()
            val findAttachments: FindAttachmentsByResourceUseCase by inject()
            val removeAttachment: RemoveAttachmentUseCase by inject()

            post {
                val submissionId = call.parameters.getUuidOrFail("submissionId")

                requireSubmissionAuthor(submissionId, call.currentUserId())

                val uploadedAttachment = addAttachment(receiveAttachment(), submissionId)
                call.respond(HttpStatusCode.Created, uploadedAttachment)
            }
            get {
                val submissionId = call.parameters.getUuidOrFail("submissionId")
                if (!isSubmissionAuthor(submissionId, call.currentUserId()))
                    requireCapability(
                        userId = call.currentUserId(),
                        capability = Capability.ReadSubmissions,
                        scopeId = call.parameters.getUuidOrFail("courseId")
                    )

                call.respond(findAttachments(submissionId))
            }
            delete("/{attachmentId}") {
                requireSubmissionAuthor(call.parameters.getUuidOrFail("submissionId"), call.currentUserId())

                removeAttachment(
                    call.parameters.getUuidOrFail("attachmentId"),
                    call.parameters.getUuidOrFail("submissionId")
                )
            }
        }
        route("/grade") {
            val setGradeSubmission: SetGradeSubmissionUseCase by inject()
            val cancelGradeSubmission: CancelGradeSubmissionUseCase by inject()
            val submissionRepository: SubmissionRepository by inject()

            put {
                val currentUserId = call.currentUserId()
                val submissionId = call.parameters.getUuidOrFail("submissionId")

                requireCapability(
                    userId = currentUserId,
                    capability = Capability.GradeSubmission,
                    scopeId = submissionRepository.findCourseIdBySubmissionId(submissionId)
                )
                val submission = setGradeSubmission(
                    grade = SubmissionGradeRequest(
                        value = call.receive<GradeRequest>().value,
                        submissionId = submissionId,
                        gradedBy = currentUserId
                    )
                )
                call.respond(HttpStatusCode.OK, submission)
            }
            delete {
                val currentUserId = call.currentUserId()
                val submissionId = call.parameters.getUuidOrFail("submissionId")

                requireCapability(
                    userId = currentUserId,
                    capability = Capability.GradeSubmission,
                    scopeId = submissionRepository.findCourseIdBySubmissionId(submissionId)
                )

                cancelGradeSubmission(submissionId)
                call.respond(HttpStatusCode.OK)
            }
        }
        post("/submit") {
            val currentUserId = call.currentUserId()
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
        post("/cancel") {
            val currentUserId = call.currentUserId()
            requireCapability(
                userId = currentUserId,
                capability = Capability.SubmitSubmission,
                scopeId = call.parameters.getUuidOrFail("courseId")
            )
            val canceledSubmission = cancelSubmission(
                submissionId = call.parameters.getUuidOrFail("submissionId"),
                studentId = currentUserId,
            )
            call.respond(HttpStatusCode.OK, canceledSubmission)
        }
    }
}

fun Route.submissionByAuthorRoute() {
    route("/submissions-by-student") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findSubmissionByStudent: FindSubmissionByStudentUseCase by inject()
        get("/{studentId}") {
            val currentUserId = call.currentUserId()
            val submission = findSubmissionByStudent(
                call.parameters.getUuidOrFail("workId"),
                call.getUserUuidByParameterOrMe("studentId"),
                currentUserId
            )

            val isOwnSubmission = submission.author.id == currentUserId
            if (isOwnSubmission) call.respond(HttpStatusCode.OK, submission)
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
}