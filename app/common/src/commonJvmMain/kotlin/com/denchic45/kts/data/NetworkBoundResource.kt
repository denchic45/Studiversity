package com.denchic45.kts.data

import com.denchic45.kts.data.domain.Cause
import com.denchic45.kts.data.repository.NetworkServiceOwner
import com.denchic45.kts.data.service.withCollectHasNetwork
import com.denchic45.kts.data.service.withHasNetwork
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.toResource
import com.denchic45.stuiversity.api.common.ResponseResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


fun <T, R> NetworkServiceOwner.observeResource(
    query: Flow<T>,
    fetch: suspend () -> ResponseResult<R>,
    saveFetch: suspend (result: R) -> Unit,
    shouldFetch: (T) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val first = query.first()
    if (shouldFetch(first) && networkService.isNetworkAvailable) {
        emit(Resource.Loading(first))
        runCatching {
            fetch()
        }.fold(onSuccess = { result ->
            result.toResource()
                .onSuccess { saveFetch(it) }
                .onFailure { emit(Resource.Error(it)) }
        }, onFailure = { emit(Resource.Error(Cause(it))) })
    }
    emitAll(query.map { Resource.Success(it) })
}

fun <T, R> NetworkServiceOwner.observeResource(
    query: Flow<T>,
    observe: () -> Flow<ResponseResult<R>>,
    saveObserved: suspend (result: R) -> Unit,
    shouldObserve: (T) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val first = query.first()
    if (shouldObserve(first)) {
        emit(Resource.Loading(first))
        coroutineScope {
            val fetchResultFlow: MutableSharedFlow<Resource<R>> = MutableSharedFlow(replay = 1)
            launch {
                networkService.observeNetwork()
                    .flatMapLatest { has -> if (has) observe() else emptyFlow() }
                    .catch { emit(Resource.Error(Cause(it))) }
                    .map { result ->
                        result.toResource()
                            .onSuccess { saveObserved(it) }
                            .onFailure { emit(Resource.Error(it)) }
                    }.collect { fetchResultFlow.emit(it) }
            }
            fetchResultFlow.first()
        }
    }
    emitAll(query.map { Resource.Success(it) })
}


suspend fun <T> NetworkServiceOwner.fetchResource(
    searchLambda: suspend () -> ResponseResult<T>,
): Resource<T> = networkService.withHasNetwork {
    searchLambda().toResource()
}

fun <T> NetworkServiceOwner.fetchObservingResource(
    searchLambda: () -> Flow<ResponseResult<T>>,
): Flow<Resource<T>> = networkService.withCollectHasNetwork {
    searchLambda().map { it.toResource() }
}