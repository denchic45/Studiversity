package com.studiversity.ktor

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.jwtPrincipal() = principal<JWTPrincipal>()!!