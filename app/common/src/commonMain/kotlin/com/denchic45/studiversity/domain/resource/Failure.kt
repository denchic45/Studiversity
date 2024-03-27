package com.denchic45.studiversity.domain.resource

import com.denchic45.studiversity.util.ErrorResponse
import io.ktor.http.HttpStatusCode

sealed class Failure
data object NoConnection : Failure()
data object Forbidden : Failure()
data object NotFound : Failure()
data object ServerError : Failure()
class ClientError(val response: ErrorResponse) : Failure()
class Cause(val throwable: Throwable) : Failure()

fun ErrorResponse.toFailure(
): Failure = when (HttpStatusCode.fromValue(code)) {
    HttpStatusCode.NotFound -> NotFound
    HttpStatusCode.Forbidden -> Forbidden
    HttpStatusCode.InternalServerError -> ServerError
    else -> ClientError(this)
}