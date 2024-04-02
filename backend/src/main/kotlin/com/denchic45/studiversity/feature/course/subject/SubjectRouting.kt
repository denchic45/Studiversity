package com.denchic45.studiversity.feature.course.subject

import com.denchic45.studiversity.api.course.subject.SubjectErrors
import com.denchic45.studiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.studiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.course.subject.usecase.*
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.CommonErrors
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.util.onlyDigits
import com.denchic45.studiversity.validation.buildValidationResult
import com.denchic45.studiversity.validation.require
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.subjectRoutes() {
    routing {
        authenticate("auth-jwt") {
            val findSubjectsIcons: FindSubjectsIconsUseCase by inject()

            route("/subjects") {
                install(RequestValidation) {
                    validate<CreateSubjectRequest> { request ->
                        buildValidationResult {
                            condition(
                                request.name.isNotEmpty() && !request.name.onlyDigits(),
                                SubjectErrors.INVALID_SUBJECT_NAME
                            )
                            condition(
                                request.iconUrl.isNotEmpty(),
                                SubjectErrors.INVALID_SUBJECT_ICON_URL
                            )
                        }
                    }
                }
                val requireCapability: RequireCapabilityUseCase by inject()
                val addSubject: AddSubjectUseCase by inject()
                val searchSubjects: SearchSubjectsUseCase by inject()

                post {
                    requireCapability(
                        call.currentUserId(),
                        Capability.WriteSubject,
                        config.organizationId
                    )
                    val subject = addSubject(call.receive())
                    call.respond(HttpStatusCode.Created, subject)
                }
                get {
                    val q = call.request.queryParameters["q"]?.require(
                        String::isNotBlank,
                        CommonErrors::PARAMETER_MUST_NOT_BE_EMPTY
                    )
                    call.respond(HttpStatusCode.OK, searchSubjects(q))
                }
                subjectByIdRoute()
            }
            get("/subjects-icons") {
                call.respond(findSubjectsIcons())
            }
        }
        route("/subject-icons") {
            val findSubjectIcon: FindSubjectIconUseCase by inject()
            get("/{iconName}") {
                val stream = findSubjectIcon(call.parameters.getOrFail("iconName"))
                call.respondOutputStream(
                    contentType = ContentType.Image.SVG,
                    producer = { stream.transferTo(this) }
                )
            }
        }
    }
}

fun Route.subjectByIdRoute() {
    route("/{subjectId}") {
        install(RequestValidation) {
            validate<UpdateSubjectRequest> { request ->
                buildValidationResult {
                    request.name.ifPresent {
                        condition(
                            it.isNotEmpty(),
                            SubjectErrors.INVALID_SUBJECT_NAME
                        )
                    }
                    request.iconUrl.ifPresent {
                        condition(
                            it.isNotEmpty(),
                            SubjectErrors.INVALID_SUBJECT_ICON_URL
                        )
                    }
                }
            }
        }
        val requireCapability: RequireCapabilityUseCase by inject()
        val findSubjectById: FindSubjectByIdUseCase by inject()
        val updateSubject: UpdateSubjectUseCase by inject()
        val removeSubject: RemoveSubjectUseCase by inject()

        get {
            val subjectId = call.parameters.getUuidOrFail("subjectId")
            call.respond(HttpStatusCode.OK, findSubjectById(subjectId))
        }
        patch {
            requireCapability(
                call.currentUserId(),
                Capability.WriteSubject,
                config.organizationId
            )

            updateSubject(call.parameters.getUuidOrFail("subjectId"), call.receive()).let { subject ->
                call.respond(HttpStatusCode.OK, subject)
            }
        }
        delete {
            requireCapability(
                call.currentUserId(),
                Capability.WriteSubject,
                config.organizationId
            )

            removeSubject(call.parameters.getUuidOrFail("subjectId"))
            call.respond(HttpStatusCode.NoContent)
        }
    }
}