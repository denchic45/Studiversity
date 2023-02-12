package com.studiversity.ktor

import io.ktor.server.application.*
import io.ktor.server.util.*
import java.util.*

fun ApplicationCall.currentUserId() = jwtPrincipal().payload.claimId

fun ApplicationCall.getUserUuidByParamOrMe(name: String): UUID {
    return when (parameters.getOrFail(name)) {
        "me" -> currentUserId()
        else -> parameters.getUuid(name)
    }
}