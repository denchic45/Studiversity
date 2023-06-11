package com.studiversity.util

import com.denchic45.stuiversity.api.common.ResponseResult
import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError

fun <T> ResponseResult<T>.unwrapAsserted() = assertedResultIsOk().unwrap()

fun <T> ResponseResult<T>.unwrapAssertedError() = assertedResultIsError().unwrapError()