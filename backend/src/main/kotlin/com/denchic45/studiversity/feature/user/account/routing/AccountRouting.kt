package com.denchic45.studiversity.feature.user.account.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.accountRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/account") {
                personalRoute()
                securityRoute()
            }
        }
    }
}