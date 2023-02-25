package com.denchic45.kts.domain

import com.denchic45.kts.data.domain.Failure
import com.denchic45.kts.data.domain.toFailure
import com.denchic45.stuiversity.api.common.ResponseResult
import com.github.michaelbull.result.mapBoth

sealed interface Resource<out T> {
    data class Loading<T>(val value:T? = null) : Resource<T>
    data class Success<T>(val value: T) : Resource<T>
    data class Error(val failure: Failure) : Resource<Nothing>
}


fun <T> Resource<T>.getData() = (this as Resource.Success).value

typealias EmptyResource = Resource<Unit>

fun <T> ResponseResult<T>.toResource(): Resource<T> = mapBoth(
    success = { Resource.Success(it) },
    failure = { Resource.Error(it.toFailure()) }
)

fun <T> ResponseResult<T>.toEmptyResource(): EmptyResource = mapBoth(
    success = { Resource.Success(Unit) },
    failure = { Resource.Error(it.toFailure()) }
)

inline infix fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> = apply {
    if (this is Resource.Success) {
        action(value)
    }
}

inline infix fun <T> Resource<T>.onFailure(action: (Failure) -> Unit): Resource<T> = apply {
    if (this is Resource.Error) {
        action(failure)
    }
}
