package com.studiversity.feature.course.element

import com.studiversity.feature.course.element.usecase.*
import com.denchic45.stuiversity.api.role.model.Capability
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.*
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.courseElementRoutes() {
    route("/elements") {
        val findCourseElementsByCourseId: FindCourseElementsByCourseIdUseCase by inject()
        val requireCapability: RequireCapabilityUseCase by inject()

        get {
            val courseId = call.parameters.getUuidOrFail("courseId")
            val sorting = call.request.queryParameters.getSortingBy(CourseElementsSorting)

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            val elements = findCourseElementsByCourseId(courseId, sorting)
            call.respond(HttpStatusCode.OK, elements)
        }
        courseElementById()
    }
}

fun Route.courseElementById() {
    route("/{elementId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseElement: FindCourseElementUseCase by inject()
        val updateCourseElement: UpdateCourseElementUseCase by inject()
        val removeCourseElement: RemoveCourseElementUseCase by inject()

        get {
            val courseId = call.parameters.getUuidOrFail("courseId")

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            val element = findCourseElement(call.parameters.getUuidOrFail("elementId"))
            call.respond(HttpStatusCode.OK, element)
        }
        patch {
            val courseId = call.parameters.getUuidOrFail("courseId")
            val elementId = call.parameters.getUuidOrFail("elementId")

            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourse,
                scopeId = courseId
            )

            val element = updateCourseElement(courseId, elementId, call.receive())
            call.respond(HttpStatusCode.OK, element)
        }
        delete {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuidOrFail("courseId")
            val workId = call.parameters.getUuidOrFail("elementId")

            requireCapability(
                userId = currentUserId,
                capability = Capability.DeleteCourseElements,
                scopeId = courseId
            )
            removeCourseElement(courseId, workId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}