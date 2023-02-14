package com.studiversity.feature.specialty

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.feature.specialty.usecase.*
import com.studiversity.ktor.currentUserId
import com.studiversity.ktor.getUuid
import com.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import java.util.*

fun Application.configureSpecialties() {
    routing {
        authenticate("auth-jwt") {
            route("/specialties") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val organizationId: UUID by inject(named(OrganizationEnv.ORG_ID))
                val addSpecialty: AddSpecialtyUseCase by inject()
                val searchSpecialties: SearchSpecialtiesUseCase by inject()

                get {
                    val q: String = call.request.queryParameters.getOrFail("q")
                    call.respond(HttpStatusCode.OK, searchSpecialties(q))
                }

                post {
                    requireCapability(call.currentUserId(), Capability.WriteSpecialty, organizationId)
                    call.respond(HttpStatusCode.Created, addSpecialty(call.receive()))
                }
                route("/{specialtyId}") {
                    val findSpecialtyById: FindSpecialtyByIdUseCase by inject()
                    val updateSpecialty: UpdateSpecialtyUseCase by inject()
                    val removeSpecialty: RemoveSpecialtyUseCase by inject()

                    get {
                        call.respond(HttpStatusCode.OK, findSpecialtyById(call.parameters.getUuid("specialtyId")))
                    }
                    patch {
                        requireCapability(call.currentUserId(), Capability.WriteSpecialty, organizationId)
                        val specialty = updateSpecialty(call.parameters.getUuid("specialtyId"), call.receive())
                        call.respond(HttpStatusCode.OK, specialty)
                    }
                    delete {
                        requireCapability(call.currentUserId(), Capability.WriteSpecialty, organizationId)
                        removeSpecialty(call.parameters.getUuid("specialtyId"))
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}