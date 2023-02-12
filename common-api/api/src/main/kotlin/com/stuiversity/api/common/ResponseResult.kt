package com.stuiversity.api.common

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.stuiversity.util.ErrorResponse
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

typealias ResponseResult<T> = Result<T, ErrorResponse>

suspend inline fun <reified T> HttpResponse.toResult(): ResponseResult<T> {
    return if (status.isSuccess()) {
        Ok(body())
    } else {
        Err(body())
    }
}

suspend inline fun <reified T> HttpResponse.toResult(result: (HttpResponse) -> T): ResponseResult<T> {
    return if (status.isSuccess()) {
        Ok(result(this))
    } else {
        Err(body())
    }
}

typealias EmptyResponseResult = Result<Unit, ErrorResponse>