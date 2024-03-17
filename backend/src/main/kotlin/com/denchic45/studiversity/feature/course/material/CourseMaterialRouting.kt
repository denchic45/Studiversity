package com.denchic45.studiversity.feature.course.material

import com.denchic45.studiversity.feature.attachment.receiveAttachment
import com.denchic45.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.denchic45.studiversity.feature.attachment.usecase.FindAttachmentsByResourceUseCase
import com.denchic45.studiversity.feature.attachment.usecase.RemoveAttachmentUseCase
import com.denchic45.studiversity.feature.course.element.CourseElementRepository
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
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.courseMaterialRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("courses/{courseId}/materials") {
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

            }
            courseMaterialById()
        }
    }
}

private fun Route.courseMaterialById() {
    route("course-materials/{materialId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findMaterial: FindCourseMaterialUseCase by inject()
        val updateMaterial: UpdateCourseMaterialUseCase by inject()
        val courseElementRepository: CourseElementRepository by inject()
        get {
            val materialId = call.parameters.getUuidOrFail("materialId")
            val courseId = courseElementRepository.findCourseIdByElementId(materialId)

            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.ReadCourseElements,
                scopeId = courseId
            )

            call.respond(HttpStatusCode.OK, findMaterial(materialId))
        }

        patch {
            val materialId = call.parameters.getUuidOrFail("materialId")
            val courseId = courseElementRepository.findCourseIdByElementId(materialId)
            requireCapability(
                userId = call.jwtPrincipal().payload.claimId,
                capability = Capability.WriteCourseElements,
                scopeId = courseId
            )
            val updatedMaterial = updateMaterial(
                courseId = courseId,
                materialId = materialId,
                request = call.receive()
            )
            call.respond(HttpStatusCode.OK, updatedMaterial)
        }

        route("/attachments") {
            val addAttachment: AddAttachmentUseCase by inject()
            val findAttachments: FindAttachmentsByResourceUseCase by inject()
            val removeAttachment: RemoveAttachmentUseCase by inject()
            post {
                val materialId = call.parameters.getUuidOrFail("materialId")

                requireCapability(
                    userId = call.currentUserId(),
                    capability = Capability.WriteCourseElements,
                    scopeId = courseElementRepository.findCourseIdByElementId(materialId)
                )

                val uploadedAttachment = addAttachment(receiveAttachment(), materialId)
                call.respond(HttpStatusCode.Created, uploadedAttachment)
            }
            get {
                val materialId = call.parameters.getUuidOrFail("materialId")
                requireCapability(
                    userId = call.currentUserId(),
                    capability = Capability.ReadCourseElements,
                    scopeId = courseElementRepository.findCourseIdByElementId(materialId)
                )

                call.respond(findAttachments(materialId))
            }
            delete("/{attachmentId}") {
                val materialId = call.parameters.getUuidOrFail("materialId")
                requireCapability(
                    userId = call.currentUserId(),
                    capability = Capability.WriteCourseElements,
                    scopeId = courseElementRepository.findCourseIdByElementId(materialId)
                )

                removeAttachment(call.parameters.getUuidOrFail("attachmentId"), materialId)
            }
        }
    }
}