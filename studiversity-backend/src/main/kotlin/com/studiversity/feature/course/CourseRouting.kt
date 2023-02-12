package com.studiversity.feature.course

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.course.element.courseElementRoutes
import com.studiversity.feature.course.topic.courseTopicsRoutes
import com.studiversity.feature.course.usecase.*
import com.studiversity.feature.course.work.courseWorksRoutes
import com.stuiversity.api.role.model.Capability
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.claimId
import com.studiversity.ktor.getUuid
import com.studiversity.ktor.jwtPrincipal
import com.studiversity.util.onlyDigits
import com.studiversity.util.toUUID
import com.studiversity.validation.buildValidationResult
import com.stuiversity.api.course.model.CreateCourseRequest
import com.stuiversity.api.course.model.UpdateCourseRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import java.util.*

fun Application.courseRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/courses") {
                install(RequestValidation) {
                    validate<CreateCourseRequest> { request ->
                        buildValidationResult {
                            condition(
                                request.name.isNotEmpty() && !request.name.onlyDigits(),
                                CourseErrors.INVALID_COURSE_NAME
                            )
                        }
                    }
                }
                val organizationId: UUID by inject(named(OrganizationEnv.ORG_ID))
                val requireCapability: RequireCapabilityUseCase by inject()
                val addCourse: AddCourseUseCase by inject()

                post {
                    val currentUserId = call.principal<JWTPrincipal>()!!.payload.getClaim("sub").asString().toUUID()

                    requireCapability(currentUserId, Capability.WriteCourse, organizationId)

                    val body = call.receive<CreateCourseRequest>()

                    val course = addCourse(body)
                    call.respond(HttpStatusCode.OK, course)
                }
                courseByIdRoutes()
            }
        }
    }
}

private fun Route.courseByIdRoutes() {
    route("/{courseId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseById: FindCourseByIdUseCase by inject()
        val updateCourse: UpdateCourseUseCase by inject()
        val removeCourse: RemoveCourseUseCase by inject()

        get {
            val courseId = call.parameters.getUuid("courseId")

            val currentUserId = call.jwtPrincipal().payload.claimId

            requireCapability(currentUserId, Capability.ReadCourse, courseId)

            findCourseById(courseId).let { course -> call.respond(HttpStatusCode.OK, course) }
        }

        patch {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuid("courseId")

            requireCapability(currentUserId, Capability.WriteCourse, courseId)

            val body = call.receive<UpdateCourseRequest>()

            updateCourse(courseId, body).let { course ->
                call.respond(HttpStatusCode.OK, course)
            }
        }
        route("/archived") {
            val archiveCourse: ArchiveCourseUseCase by inject()
            val unarchiveCourse: UnarchiveCourseUseCase by inject()

            put {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuid("courseId")

                requireCapability(currentUserId, Capability.WriteCourse, courseId)

                archiveCourse(courseId)
                call.respond(HttpStatusCode.OK)
            }
            delete {
                val courseId = call.parameters.getUuid("courseId")
                val currentUserId = call.jwtPrincipal().payload.claimId

                requireCapability(currentUserId, Capability.DeleteCourse, courseId)

                unarchiveCourse(courseId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        delete {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuid("courseId")

            requireCapability(currentUserId, Capability.WriteCourse, courseId)

            removeCourse(courseId)
            call.respond(HttpStatusCode.NoContent)
        }
        courseStudyGroups()
        courseElementRoutes()
        courseWorksRoutes()
        courseTopicsRoutes()
    }
}

private fun Route.courseStudyGroups() {
    route("/studygroups") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseStudyGroups: FindCourseStudyGroupsUseCase by inject()
        val attachStudyGroupToCourse: AttachStudyGroupToCourseUseCase by inject()
        val detachStudyGroupToCourse: DetachStudyGroupToCourseUseCase by inject()

        get {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuid("courseId")

            requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

            val studyGroupIds = findCourseStudyGroups(courseId).map(UUID::toString)
            call.respond(HttpStatusCode.OK, studyGroupIds)
        }

        route("/{studyGroupId}") {
            put {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuid("courseId")

                requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

                val studyGroupId = call.parameters["studyGroupId"]!!.toUUID()
                attachStudyGroupToCourse(courseId, studyGroupId)
                call.respond(HttpStatusCode.Created, "")
            }
            delete {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuid("courseId")

                requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

                val studyGroupId = call.parameters["studyGroupId"]!!.toUUID()
                val deleted = detachStudyGroupToCourse(studyGroupId, courseId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else call.respond(HttpStatusCode.Gone, "Study group has gone")
            }
        }
    }
}