package com.denchic45.studiversity.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondWithError(errorResponse: ErrorResponse) {
    respond(
        HttpStatusCode.fromValue(errorResponse.code),
        errorResponse
    )
}

suspend fun ApplicationCall.respondWithError(statusCode: HttpStatusCode, error: ErrorInfo) {
    respond(statusCode, ErrorResponse(statusCode.value, error))
}

suspend fun ApplicationCall.respondWithErrors(statusCode: HttpStatusCode, errors: CompositeError) {
    respond(statusCode, ErrorResponse(statusCode.value, errors))
}