package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.domain.NoConnection
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.resourceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

expect class NetworkService {
    val isNetworkAvailable: Boolean
    fun observeNetwork(): Flow<Boolean>
}

suspend fun <T> NetworkService.withHasNetwork(
    block: suspend () -> Resource<T>,
): Resource<T> = if (isNetworkAvailable) {
    block()
} else Resource.Error(NoConnection)

fun <T> NetworkService.withHasNetworkFlow(
    block: () -> Flow<Resource<T>>,
): Flow<Resource<T>> = if (isNetworkAvailable) {
    block()
} else flowOf(resourceOf(NoConnection))

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> NetworkService.withCollectHasNetwork(
    block: () -> Flow<Resource<T>>,
): Flow<Resource<T>> = observeNetwork().flatMapLatest {
    if (it) {
        block()
    } else {
        flowOf(Resource.Error(NoConnection))
    }
}