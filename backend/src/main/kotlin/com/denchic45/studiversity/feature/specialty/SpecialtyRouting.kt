package com.denchic45.studiversity.feature.specialty

import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.specialty.usecase.*
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureSpecialties() {
    routing {
        authenticate("auth-jwt") {
            route("/specialties") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val addSpecialty: AddSpecialtyUseCase by inject()
                val searchSpecialties: SearchSpecialtiesUseCase by inject()

                get {
                    val q: String = call.request.queryParameters.getOrFail("q")
                    call.respond(HttpStatusCode.OK, searchSpecialties(q))
                }

                post {
                    requireCapability(call.currentUserId(), Capability.WriteSpecialty, config.organizationId)
                    call.respond(HttpStatusCode.Created, addSpecialty(call.receive()))
                }
                route("/{specialtyId}") {
                    val findSpecialtyById: FindSpecialtyByIdUseCase by inject()
                    val updateSpecialty: UpdateSpecialtyUseCase by inject()
                    val removeSpecialty: RemoveSpecialtyUseCase by inject()

                    get {
                        call.respond(HttpStatusCode.OK, findSpecialtyById(call.parameters.getUuidOrFail("specialtyId")))
                    }
                    patch {
                        requireCapability(call.currentUserId(), Capability.WriteSpecialty, config.organizationId)
                        val specialty = updateSpecialty(call.parameters.getUuidOrFail("specialtyId"), call.receive())
                        call.respond(HttpStatusCode.OK, specialty)
                    }
                    delete {
                        requireCapability(call.currentUserId(), Capability.WriteSpecialty, config.organizationId)
                        removeSpecialty(call.parameters.getUuidOrFail("specialtyId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}