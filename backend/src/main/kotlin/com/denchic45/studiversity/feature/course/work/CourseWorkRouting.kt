package com.denchic45.studiversity.feature.course.work

import com.denchic45.studiversity.feature.attachment.attachmentRoutes
import com.denchic45.studiversity.feature.course.work.submission.workSubmissionRoutes
import com.denchic45.studiversity.feature.course.work.usecase.AddCourseWorkUseCase
import com.denchic45.studiversity.feature.course.work.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.feature.course.work.usecase.FindCourseWorksUseCase
import com.denchic45.studiversity.feature.course.work.usecase.UpdateCourseWorkUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.*
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.courseWorksRoutes2() {
    route("/course-works") {
        val findCourseWorks: FindCourseWorksUseCase by inject()
        get {
            val late = call.request.queryParameters["late"]?.toBoolean()
            val authorId = call.requireUserUuidByQueryParameterOrMe("author_id")
            val submitted = call.request.queryParameters["submitted"]?.toBoolean()

            call.respond(findCourseWorks(authorId, late, submitted))
        }
    }
}

fun Route.courseWorksRoutes() {
    route("/works") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val addCourseWork: AddCourseWorkUseCase by inject()
        post {
            val body: CreateCourseWorkRequest = call.receive()
            val courseId = call.parameters.getOrFail("courseId").toUUID()
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseElements,
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
        val findCourseWork: FindCourseWorkUseCase by inject()
        val updateCourseWork: UpdateCourseWorkUseCase by inject()

        get {
            val courseId = call.parameters.getOrFail("courseId").toUUID()

            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            val work = findCourseWork(call.parameters.getOrFail("workId").toUUID())
            call.respond(HttpStatusCode.OK, work)
        }

        patch {
            val courseId = call.parameters.getOrFail("courseId").toUUID()
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseElements,
                scopeId = courseId
            )
            val updatedWork = updateCourseWork(courseId, call.parameters.getOrFail("workId").toUUID(), call.receive())
            call.respond(HttpStatusCode.OK, updatedWork)
        }

        attachmentRoutes(
            ownerOfParameterName = "workId",
            beforePostAttachment = {
                requireCapability(
                    userId = currentUserId(),
                    capability = Capability.WriteCourseElements,
                    scopeId = parameters.getUuidOrFail("courseId")
                )
            },
            beforeGetAttachments = {
                requireCapability(
                    userId = currentUserId(),
                    capability = Capability.ReadCourseElements,
                    parameters.getUuidOrFail("courseId")
                )
            },
            beforeDeleteAttachment = {
                requireCapability(
                    userId = currentUserId(),
                    capability = Capability.WriteCourseElements,
                    scopeId = parameters.getUuidOrFail("courseId")
                )
            }
        )

        workSubmissionRoutes()
    }
}