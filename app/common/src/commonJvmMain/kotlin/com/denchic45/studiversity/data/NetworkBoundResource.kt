package com.denchic45.studiversity.data

import com.denchic45.studiversity.data.domain.Cause
import com.denchic45.studiversity.data.repository.NetworkServiceOwner
import com.denchic45.studiversity.data.service.withCollectHasNetwork
import com.denchic45.studiversity.data.service.withHasNetwork
import com.denchic45.studiversity.data.service.withHasNetworkFlow
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.loadingResource
import com.denchic45.studiversity.domain.onFailure
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.toResource
import com.denchic45.stuiversity.api.common.ResponseResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


fun <T, R> NetworkServiceOwner.observeResource(
    query: Flow<T>,
    fetch: suspend () -> ResponseResult<R>,
    saveFetch: suspend (result: R) -> Unit,
    shouldFetch: (T) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val first = query.first()
    if (shouldFetch(first) && networkService.isNetworkAvailable) {
        emit(Resource.Loading)
        runCatching {
            fetch()
        }.fold(
            onSuccess = { result ->
                result.toResource()
                    .onSuccess { saveFetch(it) }
                    .onFailure { emit(Resource.Error(it)) }
            },
            onFailure = { emit(Resource.Error(Cause(it))) })
    }
    emitAll(query.map { Resource.Success(it) })
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> NetworkServiceOwner.observeResource(
    query: Flow<T>,
    observe: () -> Flow<ResponseResult<R>>,
    saveObserved: suspend (result: R) -> Unit,
    shouldObserve: (T) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val first = query.first()
    if (shouldObserve(first)) {
        emit(Resource.Loading)
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
    block: suspend () -> ResponseResult<T>,
): Resource<T> = networkService.withHasNetwork {
    block().toResource()
}

 fun <T> NetworkServiceOwner.fetchResourceFlow(
    block: suspend () -> ResponseResult<T>,
): Flow<Resource<T>> = networkService.withHasNetworkFlow {
    flow {
        emit(loadingResource())
        emit(block().toResource())
    }
}


fun <T> NetworkServiceOwner.fetchObservingResource(
    block: () -> Flow<ResponseResult<T>>,
): Flow<Resource<T>> = networkService.withCollectHasNetwork {
    block().map { it.toResource() }
}