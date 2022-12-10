package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.error.NetworkError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface NetworkServiceOwner {
    val networkService: NetworkService

    fun <T> doOnConnection(block: () -> Flow<Result<T, NetworkError>>): Flow<Result<T, NetworkError>> {
        return if (networkService.isNetworkAvailable) {
            block()
        } else flowOf(Err(NetworkError))
    }
}