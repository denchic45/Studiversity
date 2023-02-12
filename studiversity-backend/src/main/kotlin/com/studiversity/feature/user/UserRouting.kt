package com.studiversity.feature.user

import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.auth.usecase.SignUpUserManuallyUseCase
import com.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.studiversity.feature.user.usecase.FindUserByIdUseCase
import com.studiversity.feature.user.usecase.RemoveUserUseCase
import com.studiversity.ktor.currentUserId
import com.studiversity.ktor.getUuid
import com.studiversity.util.tryToUUID
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

fun Application.userRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/users") {
                val organizationId: UUID by inject(named(OrganizationEnv.ORG_ID))
                val requireCapability: RequireCapabilityUseCase by inject()
                val signUpUserManually: SignUpUserManuallyUseCase by inject()

                post {
                    requireCapability(
                        userId = call.currentUserId(),
                        capability = Capability.WriteUser,
                        scopeId = organizationId
                    )
                    call.respond(HttpStatusCode.Created, signUpUserManually(call.receive()))
                }
                userByIdRoute()
            }
        }
    }
}

private fun Route.userByIdRoute() {
    route("/{userId}") {
        val organizationId: UUID by inject(named(OrganizationEnv.ORG_ID))
        val requireCapability: RequireCapabilityUseCase by inject()
        val findUserById: FindUserByIdUseCase by inject()
        val removeUser: RemoveUserUseCase by inject()

        get {
            val currentUserId = call.currentUserId()
            val userId = when (val parameter = call.parameters.getOrFail("userId")) {
                "me" -> currentUserId
                else -> parameter.tryToUUID()
            }
            val user = findUserById(userId)
            call.respond(HttpStatusCode.OK, user)
        }

        delete {
            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.DeleteUser,
                scopeId = organizationId
            )

            removeUser(call.parameters.getUuid("userId"))
            call.respond(HttpStatusCode.NoContent)
        }
    }
}


