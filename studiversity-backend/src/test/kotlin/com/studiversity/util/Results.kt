package com.studiversity.util

import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError
import com.denchic45.stuiversity.api.common.ResponseResult

fun <T> ResponseResult<T>.unwrapAsserted() = assertedResultIsOk().unwrap()

fun <T> ResponseResult<T>.unwrapAssertedError() = assertedResultIsError().unwrapError()