package com.denchic45.studiversity.domain

import com.denchic45.studiversity.data.domain.Cause
import com.denchic45.studiversity.data.domain.ClientError
import com.denchic45.studiversity.data.domain.Failure
import com.denchic45.studiversity.data.domain.Forbidden
import com.denchic45.studiversity.data.domain.NoConnection
import com.denchic45.studiversity.data.domain.NotFound
import com.denchic45.studiversity.data.domain.ServerError
import com.denchic45.studiversity.data.domain.toFailure
import com.denchic45.stuiversity.api.common.ResponseResult
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

sealed interface Resource<out T> {
    object Loading : Resource<Nothing>
    data class Success<T>(val value: T) : Resource<T>
    data class Error(val failure: Failure) : Resource<Nothing>
}


typealias EmptyResource = Resource<Unit>

fun loadingResource() = Resource.Loading

fun emptyResource(): EmptyResource = Resource.Success(Unit)

fun <T> resourceOf(value: T) = Resource.Success(value)

fun resourceOf(failure: Failure) = Resource.Error(failure)

fun resourceOf() = loadingResource()


fun <T> Resource<T>.success() = this as Resource.Success

fun <T> ResponseResult<T>.toResource(): Resource<T> = mapBoth(
    success = { Resource.Success(it) },
    failure = {
        Resource.Error(it.toFailure()).apply {
            when (val failure = failure) {
                is Cause -> failure.throwable.printStackTrace()
                is ClientError -> println("Client error: ${failure.response.code} ${failure.response.error}")
                Forbidden -> println("Forbidden")
                NoConnection -> println("NoConnection")
                NotFound -> println("NotFound")
                ServerError -> println("ServerError")
            }
        }
    }
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

inline infix fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> = apply {
    if (this is Resource.Loading) {
        action()
    }
}

fun <T> Resource<T>.takeValueIfSuccess(): T? = run {
    if (this is Resource.Success) value else null
}

inline infix fun <T, V> Resource<T>.ifSuccess(function: (T) -> V): V? = run {
    if (this is Resource.Success) {
        function(value)
    } else null
}


inline fun <T, V> Resource<T>.map(transform: (T) -> V): Resource<V> {
    return when (this) {
        is Resource.Error -> this
        is Resource.Loading -> this
        is Resource.Success -> Resource.Success(transform(value))
    }
}

inline fun <T, V> Resource<T>.mapResource(function: (T) -> Resource<V>): Resource<V> {
    return when (this) {
        is Resource.Error -> this
        is Resource.Loading -> this
        is Resource.Success -> function(value)
    }
}

fun <T> Resource<T?>.notNullOrFailure(error: Failure = NotFound): Resource<T> {
    return when (this) {
        is Resource.Error -> this
        is Resource.Loading -> this
        is Resource.Success -> value?.let {
            Resource.Success(it)
        } ?: Resource.Error(error)
    }
}

inline fun <T> Resource<T>.mapError(transform: (Failure) -> Failure): Resource<T> {
    return when (this) {
        is Resource.Error -> Resource.Error(transform(failure))
        is Resource.Loading,
        is Resource.Success,
        -> this
    }
}

fun <T, V> combine(resource1: Resource<T>, resource2: Resource<V>): Resource<Pair<T, V>> {
    return resource1.mapResource { value1 ->
        resource2.map { value2 ->
            value1 to value2
        }
    }
}

//fun <T, V> Resource<T>.combine(resource2: Resource<V>): Resource<Pair<T, V>> {
//    return combine(this, resource2)
//}

fun <T> Flow<Resource<T>>.filterResource(
    onSuccess: suspend (T) -> Boolean = { true },
    onError: suspend (Failure) -> Boolean = { true },
): Flow<Resource<T>> = filter {
    when (it) {
        is Resource.Success -> onSuccess(it.value)
        is Resource.Error -> onError(it.failure)
        Resource.Loading -> true
    }
}

fun <T> Flow<Resource<T>>.filterSuccess(): Flow<Resource.Success<T>> = filterIsInstance()

fun <T> Flow<Resource<T?>>.filterNotNullValue(): Flow<Resource<T>> = filter { resource ->
    resource.ifSuccess { it != null } ?: true
}.map { it.notNullOrFailure() }

fun <T> Flow<Resource.Success<T>>.mapToValue(): Flow<T> = map { it.value }

fun <T> Flow<Resource<T>>.updateResource(onSuccess: (T) -> T): Flow<Resource<T>> = map {
    when (it) {
        is Resource.Error -> it
        is Resource.Loading -> it
        is Resource.Success -> Resource.Success(onSuccess(it.value))
    }
}

inline fun <T, V> Flow<Resource<T>>.mapResource(crossinline function: suspend (T) -> V): Flow<Resource<V>> =
    map {
        when (it) {
            is Resource.Error -> it
            is Resource.Loading -> it
            is Resource.Success -> Resource.Success(function(it.value))
        }
    }

fun <T, V> Flow<Resource<T>>.flatMapResource(function: (T) -> Resource<V>): Flow<Resource<V>> {
    return map { it.mapResource(function) }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, V> Flow<Resource<T>>.flatMapResourceFlow(function: (T) -> Flow<Resource<V>>): Flow<Resource<V>> {
    return flatMapLatest {
        when (it) {
            is Resource.Error -> flowOf(it)
            is Resource.Loading -> flowOf(it)
            is Resource.Success -> function(it.value)
        }
    }
}

fun <T> Flow<Resource<T?>>.notNullOrFailure(error: Failure = NotFound): Flow<Resource<T>> {
    return map { it.notNullOrFailure() }
}

fun <T> Flow<Resource<T>>.stateInResource(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Lazily,
    initialValue: Resource<T> = Resource.Loading,
): StateFlow<Resource<T>> = stateIn(scope, started, initialValue)