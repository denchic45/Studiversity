package com.denchic45.studiversity.feature.role

import com.denchic45.studiversity.feature.role.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.ktor.getUserUuidByParameterOrMe
import com.denchic45.studiversity.ktor.getUuidOrFail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.capabilitiesRoutes() {
    route("/users/{userId}/scopes/{scopeId}/capabilities") {
        val checkCapabilitiesByUserInScope: CheckUserCapabilitiesInScopeUseCase by inject()
        post("/check") {
            val userId = call.getUserUuidByParameterOrMe("userId")
            val scopeId = call.parameters.getUuidOrFail("scopeId")
            val capabilities: List<String> = call.receive()

            call.respond(HttpStatusCode.OK, checkCapabilitiesByUserInScope(userId, scopeId, capabilities))
        }
    }
}