package com.studiversity.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/test") {

                val principal = call.principal<JWTPrincipal>()
                val sub = principal!!.payload.getClaim("sub").asString()

                call.respondText("Test completed! $sub")
            }
            get("/fetch") {
            }
        }
    }
}
