package com.studiversity.ktor

import com.studiversity.util.tryToUUID
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import java.util.*

fun ApplicationCall.currentUserId() = jwtPrincipal().payload.claimId

fun ApplicationCall.getUserUuidByParameterOrMe(name: String): UUID {
    return when (val value = parameters.getOrFail(name)) {
        "me" -> currentUserId()
        else -> value.tryToUUID()
    }
}

fun ApplicationCall.getUserUuidByQueryParameterOrMe(name: String): UUID? {
    return when (val value = request.queryParameters[name]) {
        "me" -> currentUserId()
        else -> value?.tryToUUID()
    }
}

fun ApplicationCall.requireUserUuidByQueryParameterOrMe(name: String): UUID {
    return getUserUuidByQueryParameterOrMe(name) ?: throw MissingRequestParameterException(name)
}