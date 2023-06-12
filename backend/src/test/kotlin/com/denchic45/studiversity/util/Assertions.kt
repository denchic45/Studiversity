package com.denchic45.studiversity.util

import com.denchic45.stuiversity.api.common.ResponseResult
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError
import org.junit.jupiter.api.Assertions.assertNotNull

fun assertResultIsOk(result: ResponseResult<*>) {
    assertNotNull(result.get()) { "status: " + result.unwrapError().code.toString() + " reason: " + result.unwrapError().error.toString() }
}

fun assertResultIsError(result: ResponseResult<*>) {
    assertNotNull(result.getError()) { result.unwrap().toString() }
}

fun <T> ResponseResult<T>.assertedResultIsOk() = apply(::assertResultIsOk)

fun <T> ResponseResult<T>.assertedResultIsError() = apply(::assertResultIsError)