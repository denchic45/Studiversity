package com.studiversity.feature.course.subject

import com.studiversity.config
import com.studiversity.feature.course.subject.usecase.*
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.ktor.claimId
import com.studiversity.ktor.jwtPrincipal
import com.studiversity.util.onlyDigits
import com.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import com.studiversity.ktor.CommonErrors
import com.studiversity.validation.require
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.subjectRoutes() {
    routing {
        authenticate("auth-jwt") {
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
                                SubjectErrors.INVALID_SUBJECT_ICON_NAME
                            )
                        }
                    }
                }
                val requireCapability: RequireCapabilityUseCase by inject()
                val addSubject: AddSubjectUseCase by inject()
                val searchSubjects: SearchSubjectsUseCase by inject()

                post {
                    requireCapability(
                        call.jwtPrincipal().payload.claimId,
                        Capability.WriteSubject,
                        config.organization.id
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
        }
    }
}

fun Route.subjectByIdRoute() {
    route("/{id}") {
        install(RequestValidation) {
            validate<UpdateSubjectRequest> { request ->
                buildValidationResult {
                    request.name.ifPresent {
                        condition(
                            it.isNotEmpty() && !it.onlyDigits(),
                            SubjectErrors.INVALID_SUBJECT_NAME
                        )
                    }
                    request.iconUrl.ifPresent {
                        condition(
                            it.isNotEmpty(),
                            SubjectErrors.INVALID_SUBJECT_ICON_NAME
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
            val id = call.parameters["id"]!!.toUUID()

            requireCapability(
                call.jwtPrincipal().payload.claimId,
                Capability.ReadSubject,
                config.organization.id
            )

            findSubjectById(id).let { subject ->
                call.respond(HttpStatusCode.OK, subject)
            }
        }
        patch {
            val id = call.parameters["id"]!!.toUUID()

            requireCapability(
                call.jwtPrincipal().payload.claimId,
                Capability.WriteSubject,
                config.organization.id
            )

            val body = call.receive<UpdateSubjectRequest>()
            updateSubject(id, body).let { subject ->
                call.respond(HttpStatusCode.OK, subject)
            }
        }
        delete {
            val id = call.parameters["id"]!!.toUUID()

            requireCapability(
                call.jwtPrincipal().payload.claimId,
                Capability.DeleteSubject,
                config.organization.id
            )

            removeSubject(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}