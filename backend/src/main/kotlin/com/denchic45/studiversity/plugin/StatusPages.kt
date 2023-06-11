package com.denchic45.studiversity.plugin

import com.denchic45.studiversity.ktor.ConflictException
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.studiversity.util.respondWithError
import com.denchic45.studiversity.util.respondWithErrors
import com.denchic45.stuiversity.util.ErrorInfo
import com.denchic45.stuiversity.util.toErrors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, exception ->
            call.respondWithError(
                HttpStatusCode.BadRequest,
                ErrorInfo(
                    if (exception.message == "Illegal input") {
                        "INVALID_INPUT"
                    } else exception.message ?: ""
                )
            )
        }
        exception<NotFoundException> { call, exception ->
            call.respondWithError(HttpStatusCode.NotFound, ErrorInfo(exception.message ?: ""))
        }
        exception<ForbiddenException> { call, exception ->
            call.respondWithError(HttpStatusCode.Forbidden, ErrorInfo(exception.message ?: ""))
        }
        exception<RequestValidationException> { call, exception ->
            call.respondWithErrors(HttpStatusCode.BadRequest, exception.reasons.toErrors())
        }
        exception<ConflictException> { call, exception ->
            call.respondWithError(HttpStatusCode.Conflict, ErrorInfo(exception.message ?: ""))
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondWithError(HttpStatusCode.NotFound, ErrorInfo("NOT_FOUND"))
        }
        status(HttpStatusCode.InternalServerError) { call, status ->
            call.respondWithError(HttpStatusCode.InternalServerError, ErrorInfo("INTERNAL_SERVER"))
        }
    }
}