package com.denchic45.studiversity.feature.course.material

import com.denchic45.studiversity.feature.attachment.attachmentRoutes
import com.denchic45.studiversity.feature.course.material.usecase.AddCourseMaterialUseCase
import com.denchic45.studiversity.feature.course.material.usecase.FindCourseMaterialUseCase
import com.denchic45.studiversity.feature.course.material.usecase.UpdateCourseMaterialUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.claimId
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.ktor.jwtPrincipal
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.courseMaterialsRoutes() {
    route("/materials") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val addCourseMaterial: AddCourseMaterialUseCase by inject()

        post {
            val body = call.receive<CreateCourseMaterialRequest>()
            val courseId = call.parameters.getOrFail("courseId").toUUID()
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseElements,
                scopeId = courseId
            )
            addCourseMaterial(courseId, body).let { courseElement ->
                call.respond(courseElement)
            }
        }
        courseMaterialById()
    }
}

private fun Route.courseMaterialById() {
    route("/{materialId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findMaterial: FindCourseMaterialUseCase by inject()
        val updateMaterial: UpdateCourseMaterialUseCase by inject()

        get {
            val courseId = call.parameters.getOrFail("courseId").toUUID()

            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            val material = findMaterial(call.parameters.getOrFail("materialId").toUUID())
            call.respond(HttpStatusCode.OK, material)
        }

        patch {
            val courseId = call.parameters.getOrFail("courseId").toUUID()
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseElements,
                scopeId = courseId
            )
            val updatedMaterial = updateMaterial(
                courseId = courseId,
                materialId = call.parameters.getOrFail("materialId").toUUID(),
                request = call.receive()
            )
            call.respond(HttpStatusCode.OK, updatedMaterial)
        }

        attachmentRoutes(
            ownerOfParameterName = "materialId",
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
    }
}