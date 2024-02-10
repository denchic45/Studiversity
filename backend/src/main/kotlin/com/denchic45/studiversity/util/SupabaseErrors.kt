package com.denchic45.studiversity.util

import com.denchic45.stuiversity.util.CompositeError
import com.denchic45.stuiversity.util.ErrorInfo
import com.denchic45.stuiversity.util.ErrorResponse
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