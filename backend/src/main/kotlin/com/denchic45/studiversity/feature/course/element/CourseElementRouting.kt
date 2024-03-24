package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementsByCourseIdUseCase
import com.denchic45.studiversity.feature.course.element.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.UpdateCourseElementUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getSortingBy
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.courseElementRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("courses/{courseId}/elements") {
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
            }
            courseElementById()
        }
    }
}

fun Route.courseElementById() {
    route("course-elements/{elementId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseElement: FindCourseElementUseCase by inject()
        val updateCourseElement: UpdateCourseElementUseCase by inject()
        val removeCourseElement: RemoveCourseElementUseCase by inject()
        val courseElementRepository: CourseElementRepository by inject()

        get {
            val elementId = call.parameters.getUuidOrFail("elementId")

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.ReadCourseElements,
                scopeId = courseElementRepository.findCourseIdByElementId(elementId)
            )


            val element = findCourseElement(elementId)
            call.respond(HttpStatusCode.OK, element)
        }
        patch {
            val elementId = call.parameters.getUuidOrFail("elementId")

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourse,
                scopeId = courseElementRepository.findCourseIdByElementId(elementId)
            )

            val element = updateCourseElement(elementId, call.receive())
            call.respond(HttpStatusCode.OK, element)
        }
        delete {
            val elementId = call.parameters.getUuidOrFail("elementId")

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.DeleteCourseElements,
                scopeId = courseElementRepository.findCourseIdByElementId(elementId)
            )
            removeCourseElement(elementId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}