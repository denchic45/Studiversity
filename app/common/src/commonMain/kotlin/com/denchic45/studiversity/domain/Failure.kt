package com.denchic45.studiversity.domain

import com.denchic45.stuiversity.util.ErrorResponse
import io.ktor.http.HttpStatusCode

sealed class Failure
object NoConnection : Failure()
object Forbidden : Failure()
object NotFound : Failure()
object ServerError : Failure()
class ClientError(val response: ErrorResponse) : Failure()
class Cause(val throwable: Throwable) : Failure()

fun ErrorResponse.toFailure(
): Failure = when (HttpStatusCode.fromValue(code)) {
    HttpStatusCode.NotFound -> NotFound
    HttpStatusCode.Forbidden -> Forbidden
    HttpStatusCode.InternalServerError -> ServerError
    else -> ClientError(this)
}