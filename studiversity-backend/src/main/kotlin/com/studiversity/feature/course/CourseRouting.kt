package com.studiversity.feature.course

import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import com.studiversity.config
import com.studiversity.feature.course.element.courseElementRoutes
import com.studiversity.feature.course.topic.courseTopicsRoutes
import com.studiversity.feature.course.usecase.*
import com.studiversity.feature.course.work.courseWorksRoutes
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.*
import com.studiversity.util.onlyDigits
import com.studiversity.validation.buildValidationResult
import com.studiversity.validation.require
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                val requireCapability: RequireCapabilityUseCase by inject()
                val addCourse: AddCourseUseCase by inject()
                val searchCourses: SearchCoursesUseCase by inject()

                get {
                    val q = call.request.queryParameters["q"]?.require(
                        String::isNotBlank,
                        CommonErrors::PARAMETER_MUST_NOT_BE_EMPTY
                    )
                    val memberId = call.getUserUuidByQueryParameterOrMe("member_id")
                    val subjectId = call.request.queryParameters.getUuid("subject_id")
                    call.respond(HttpStatusCode.OK, searchCourses(q, memberId, subjectId))
                }

                post {
                    val currentUserId = call.principal<JWTPrincipal>()!!.payload.getClaim("sub").asString().toUUID()

                    requireCapability(currentUserId, Capability.WriteCourse, config.organization.id)

                    val body = call.receive<CreateCourseRequest>()

                    val course = addCourse(body)
                    call.respond(HttpStatusCode.OK, course)
                }
                courseByIdRoutes()
            }
            get("/me/courses") {
                call.respondRedirect {
                    appendPathSegments("courses")
                    parameters.append("member_id", call.currentUserId().toString())
                }
            }
            studentWorksRoute()
        }
    }
}

fun Route.studentWorksRoute() {
    get {
        val studentId = call.getUserUuidByQueryParameterOrMe("student_id")
        val overdue = call.request.queryParameters["overdue"]?.toBoolean()
        val statuses = call.request.queryParameters.getAll("status")?.map { SubmissionState.valueOf(it) }

        // TODO: complete
    }
}

private fun Route.courseByIdRoutes() {
    route("/{courseId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseById: FindCourseByIdUseCase by inject()
        val updateCourse: UpdateCourseUseCase by inject()
        val removeCourse: RemoveCourseUseCase by inject()

        get {
            val courseId = call.parameters.getUuidOrFail("courseId")

            val currentUserId = call.jwtPrincipal().payload.claimId

            requireCapability(currentUserId, Capability.ReadCourse, courseId)

            findCourseById(courseId).let { course -> call.respond(HttpStatusCode.OK, course) }
        }

        patch {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuidOrFail("courseId")

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
                val courseId = call.parameters.getUuidOrFail("courseId")

                requireCapability(currentUserId, Capability.WriteCourse, courseId)

                archiveCourse(courseId)
                call.respond(HttpStatusCode.OK)
            }
            delete {
                val courseId = call.parameters.getUuidOrFail("courseId")
                val currentUserId = call.jwtPrincipal().payload.claimId

                requireCapability(currentUserId, Capability.DeleteCourse, courseId)

                unarchiveCourse(courseId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        delete {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val courseId = call.parameters.getUuidOrFail("courseId")

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
            val courseId = call.parameters.getUuidOrFail("courseId")

            requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

            val studyGroupIds = findCourseStudyGroups(courseId).map(UUID::toString)
            call.respond(HttpStatusCode.OK, studyGroupIds)
        }

        route("/{studyGroupId}") {
            put {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuidOrFail("courseId")

                requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

                val studyGroupId = call.parameters["studyGroupId"]!!.toUUID()
                attachStudyGroupToCourse(courseId, studyGroupId)
                call.respond(HttpStatusCode.OK)
            }
            delete {
                val currentUserId = call.jwtPrincipal().payload.claimId
                val courseId = call.parameters.getUuidOrFail("courseId")

                requireCapability(currentUserId, Capability.WriteCourseStudyGroups, courseId)

                val studyGroupId = call.parameters["studyGroupId"]!!.toUUID()
                detachStudyGroupToCourse(studyGroupId, courseId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}