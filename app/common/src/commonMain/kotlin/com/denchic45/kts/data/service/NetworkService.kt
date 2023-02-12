package com.denchic45.kts.data.service

import com.denchic45.kts.domain.error.NetworkError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

expect class NetworkService {
    val isNetworkAvailable: Boolean
    fun observeNetwork(): Flow<Boolean>
}

fun <T, E> NetworkService.withHasNetwork(
    block: () -> Result<T, E>
): Result<T, E> = if (isNetworkAvailable) {
    block()
} else Err(NetworkError) as Result<T, E>

fun <T, E> NetworkService.withCollectHasNetwork(
    block: () -> Flow<Result<T, E>>
): Flow<Result<T, E>> = observeNetwork().flatMapLatest {
    if (it) {
        block()
    } else {
        flowOf(Err(NetworkError) as Result<T, E>)
    }
}