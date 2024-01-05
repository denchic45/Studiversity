package com.denchic45.studiversity.feature.studygroup

import com.denchic45.studiversity.feature.studygroup.member.studyGroupMembers
import com.denchic45.studiversity.feature.studygroup.usecase.*
import com.denchic45.studiversity.ktor.CommonErrors
import com.denchic45.studiversity.ktor.getUserUuidByQueryParameterOrMe
import com.denchic45.studiversity.ktor.getUuid
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.validation.buildValidationResult
import com.denchic45.studiversity.validation.require
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.stuiversity.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.studyGroupRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/studygroups") {
                val addStudyGroup: AddStudyGroupUseCase by inject()
                val searchStudyGroups: SearchStudyGroupsUseCase by inject()

                install(RequestValidation) {
                    validate<CreateStudyGroupRequest> { request ->
                        buildList {
                            if (request.name.isEmpty())
                                add(StudyGroupErrors.INVALID_GROUP_NAME)

                            if (request.academicYear.run { start > end })
                                add(StudyGroupErrors.INVALID_ACADEMIC_YEAR)

                        }.let { errors ->
                            if (errors.isEmpty())
                                ValidationResult.Valid
                            else ValidationResult.Invalid(errors)
                        }
                    }
                    validate<UpdateStudyGroupRequest> { request ->
                        buildValidationResult {
                            request.name.ifPresent {
                                condition(it.isNotEmpty(), StudyGroupErrors.INVALID_GROUP_NAME)
                            }
                            request.academicYear.ifPresent {
                                condition(it.run { start <= end }, StudyGroupErrors.INVALID_ACADEMIC_YEAR)
                            }
                        }
                    }
                }
                get {
                    val q = call.request.queryParameters["q"]?.require(
                        String::isNotBlank,
                        CommonErrors::PARAMETER_MUST_NOT_BE_EMPTY
                    )
                    val memberId = call.getUserUuidByQueryParameterOrMe("member_id")
                    val roleId = call.request.queryParameters["role_id"]?.toLong()
                    val specialtyId = call.request.queryParameters.getUuid("specialty_id")
                    val academicYear = call.request.queryParameters["academic_year"]?.toInt()

                    call.respond(HttpStatusCode.OK, searchStudyGroups(q, memberId, roleId, specialtyId, academicYear))
                }
                post {
                    val body = call.receive<CreateStudyGroupRequest>()
                    val response = addStudyGroup(body)
                    call.respond(HttpStatusCode.Created, response)
                }
                studyGroupByIdRoutes()
            }
        }
    }
}

private fun Route.studyGroupByIdRoutes() {
    route("/{studyGroupId}") {
        val findStudyGroupById: FindStudyGroupByIdUseCase by inject()
        val updateStudyGroup: UpdateStudyGroupUseCase by inject()
        val removeStudyGroup: RemoveStudyGroupUseCase by inject()

        get {
            val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")
            call.respond(HttpStatusCode.OK, findStudyGroupById(studyGroupId))
        }
        patch {
            val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")
            val body = call.receive<UpdateStudyGroupRequest>()
            call.respond(HttpStatusCode.OK, updateStudyGroup(studyGroupId, body))
        }
        delete {
            val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")
            removeStudyGroup(studyGroupId)
            call.respond(HttpStatusCode.NoContent)
        }
        studyGroupMembers()
    }
}